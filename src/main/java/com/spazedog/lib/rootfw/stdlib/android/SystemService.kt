/*
 * This file is part of the RootFW Project: https://github.com/spazedog/rootfw
 *
 * Copyright (c) 2017 Daniel Bergløv, License: MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.spazedog.lib.rootfw.stdlib.android

import android.content.Context
import android.os.*
import android.util.Base64
import com.spazedog.lib.rootfw.Shell
import com.spazedog.lib.rootfw.ShellStream
import com.spazedog.lib.rootfw.stdlib.io.File
import java.io.*


/**
 * Connect to a system service and get access to all it's IPC method
 *
 * Android has a great deal of hidden methods in their IPC interfaces,
 * and also a lot of methods with restricted access that normal apps cannot
 * get permission for. This class can access any method defined in any
 * IPC interface for any system service on Android, as root. It uses
 * a small native binary from the Anycall project to call the services
 * using a root shell via [ShellStream].
 *
 * Get more access without the need to turn to Xposed or similar.
 * This only requires a device with root access.
 *
 * @constructor
 *      Throw [RuntimeException] on error, like when an interface file could not be found.
 *
 * @param context
 *      An Android [Context]
 *
 * @param stream
 *      A [ShellStream] to run Anycall from
 *
 * @param service
 *      The service to connect to, f.eks. [Context.POWER_SERVICE]
 *
 * @param iface
 *     Full name of the AIDL class that is used with the service
 */
class SystemService(context: Context, stream: ShellStream, service: String, iface: String) {

    companion object {
        /**
         * A list of all supported ABI's by this device.
         * The list is ordered by the most preferred one first.
         */
        val ABIS: Array<String> =
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    var api64 = Build.SUPPORTED_64_BIT_ABIS.joinToString(",")
                    var api32 = Build.SUPPORTED_32_BIT_ABIS.joinToString(",")

                    if (api64.contains("arm64")) {
                        api64 += ",arm64"
                    }

                    if (api32.contains("armabi")) {
                        api32 += ",arm"
                    }

                    if (api64.length > 0 && api32.length > 0) {
                        "$api64,$api32".split(',').toTypedArray()

                    } else if (api64.length > 0) {
                        "$api64".split(',').toTypedArray()

                    } else {
                        "$api32".split(',').toTypedArray()
                    }

                } else {
                    var abis = arrayOf(Build.CPU_ABI, Build.CPU_ABI2)
                    var list = ""

                    for (abi in abis) {
                        if (list.length > 0) {
                            list += ","
                        }

                        list += abi

                        if (abi.contains("arm64")) {
                            list += ",arm64"

                        } else if (abi.contains("armabi")) {
                            list += ",arm"
                        }
                    }

                    list.split(',').toTypedArray()
                }

        /**
         *
         */
        internal var INSTALL = true
    }

    /** * */
    private val mService: String = service

    /** * */
    private val mInterface: String = iface

    /** * */
    private val mContext: Context = context.applicationContext

    /** * */
    private val mStream: ShellStream = stream

    /** * */
    private val mLock = Any()

    /** * */
    private var mDaemon = false

    /** * */
    private var mActive = true

    /** * */
    private val mBinary: String = context.getFileStreamPath("anycall").absolutePath

    /** * */
    private val mTransCodes = mutableMapOf<String, Int>()

    /**
     * @suppress
     */
    init {
        try {
            Class.forName("$iface\$Stub")

        } catch (e: Throwable) {
            throw RuntimeException("Could not find the service interface class $iface")
        }

        if (INSTALL) {
            install()
        }

        mStream.connect(false, true)
    }

    /**
     * Create a new [Shell] instance using a new default [ShellStream]
     *
     * @param context
     *      An Android [Context]
     *
     * @param service
     *      The service to connect to, f.eks. [Context.POWER_SERVICE]
     *
     * @param iface
     *     Full name of the AIDL class that is used with the service
     */
    constructor(context: Context, service: String, iface: String) : this(context, { val stream = ShellStream(); stream.connect(true, true); stream }(), service, iface)

    /**
     * Get the [ShellStream] that is being used by this instance
     */
    fun getStream() = mStream

    /**
     * Name of the Android service that we are connected to
     */
    fun service() = mService

    /**
     * Check if this instance if active, that is, whether we have a shell connection or not
     */
    fun active() = synchronized(mLock) {
        mActive && mStream.isConnected()
    }

    /**
     * Check whether this instance is running in daemon or request mode
     */
    fun daemon() = synchronized(mLock) {
        mDaemon && active() && request("ping").startsWith("INFO_")
    }

    /**
     * Switch between daemon state and request state
     *
     * In daemon state, the [ShellStreamer] is permanently connected to
     * an Android service, providing dirrect and fast access to it. This
     * is useful if you make constant calls to a service over a long period of time.
     *
     * In request mode, each call to the service will connect to the service,
     * handle the request and disconnect again. This is best if you only make
     * a call ones in awhile.
     *
     * Note that daemon mode will block the [ShellStream] from any further use
     * as long as it's connected to the Android service. If you are using the
     * [ShellStream] for other things, like a [Shell], then use either request mode
     * or use a different [ShellStream]
     *
     * Also note that each switch between daemon/request mode could kill the [ShellStream]
     * if any errors occures during the switch. This will close any [Shell] instances
     * connected through the [ShellStream].
     *
     * @param flag
     *      `True` to switch to daemon mode, `False` to switch to request mode
     */
    fun daemon(flag: Boolean): Boolean {
        synchronized(mLock) {
            if (flag != mDaemon) {
                if (flag && !daemon()) {
                    // Start daemon
                    if (!active() || !request("$mBinary $mService").startsWith("INFO_")) {
                        return false
                    }

                } else {
                    // Stop daemon
                    request("exit")
                }

                // Daemon state changed successfully
                mDaemon = flag
            }

            return true
        }
    }

    /**
     * Close the daemon, if in daemon mode and cleanup this instance
     *
     * The [ShellStream] will be left active
     */
    fun close() {
        synchronized(mLock) {
            if (daemon()) {
                mStream.writeLines("exit")
            }

            mActive = false
        }
    }

    /**
     * Destroy everything, incl. the [ShellStream]
     */
    fun destroy() {
        synchronized(mLock) {
            if (active()) {
                close()
                mStream.destroy()
            }
        }
    }

    /**
     * Invoke an IPC method on the system service that this instance is connected to
     *
     * Note that the return state [Boolean] is only an indication on whether or not the call
     * made it to the service. Any errors from the service itself is service specific and can be
     * checked via the reply parcel.
     *
     * @param method
     *      The method to call
     *
     * @param reply
     *      A reply parcel that will be populated with the return data from the method.
     *      Can be `NULL` if no reply is wanted.
     *
     * @param args
     *      Arguments to be parsed to the method. A parcel will be created for the arguments.
     *      You can also parse your own populated parcel that will be used instead.
     */
    fun invoke(method: String, reply: Parcel?, vararg args: Any): Boolean {
        synchronized(mLock) {
            if (!active()) {
                throw RuntimeException("Cannot execute a closed service (ID: ${mStream.streamId()})")
            }

            val parcParsed = args.size == 1 && args[0] is Parcel
            val request = if (parcParsed) args[0] as Parcel else Parcel.obtain();

            if (request.dataPosition() == 0) {
                request.writeInterfaceToken(mInterface);
            }

            if (args.size > 1 || args.size == 0 || args[0] !is Parcel) {
                loop@
                for (arg in args) {
                    when {
                        arg is String -> request.writeString(arg)
                        arg is Int -> request.writeInt(arg)
                        arg is Long -> request.writeLong(arg)
                        arg is Double -> request.writeDouble(arg)
                        arg is Boolean -> request.writeInt(if (arg) 1 else 0)
                        arg is Map<*, *> -> request.writeMap(arg)
                        arg is List<*> -> request.writeList(arg)
                        arg is Bundle -> request.writeBundle(arg)
                        arg is Parcelable -> request.writeParcelable(arg, 0)
                        arg is IBinder -> request.writeStrongBinder(arg)
                        arg is FileDescriptor -> request.writeFileDescriptor(arg)
                        arg is CharArray -> request.writeCharArray(arg)
                        arg is ByteArray -> request.writeByteArray(arg)
                        arg is IntArray -> request.writeIntArray(arg)
                        arg is LongArray -> request.writeLongArray(arg)
                        arg is DoubleArray -> request.writeDoubleArray(arg)
                        arg is BooleanArray -> request.writeBooleanArray(arg)

                        else -> {
                            /*
                         * Kotlin claims to work with Java/Android, but often it does not.
                         * No generic type check, so no way to check for String[] and such
                         */
                            if (arg is Array<*> && arg.size > 0) {
                                val aarg = arg[0]

                                when {
                                    aarg is String -> {
                                        request.writeStringArray(arg as Array<String>); continue@loop
                                    }
                                    aarg is Parcelable -> {
                                        request.writeParcelableArray(arg as Array<Parcelable>, 0); continue@loop
                                    }
                                }
                            }

                            request.writeValue(arg)
                        }
                    }
                }
            }

            var data = Base64.encodeToString(request.marshall(), Base64.NO_WRAP)
            val key = "$mService#$method"
            var code = mTransCodes.get(key) ?: -1

            try {
                if (code == -1) {
                    val clazz = Class.forName("$mInterface\$Stub")
                    val field = clazz.getDeclaredField("TRANSACTION_$method")

                    field.isAccessible = true
                    code = field.getInt(clazz)

                    mTransCodes.put(key, code)
                }

                val response = if (mDaemon)
                    request("$code $data")
                else
                    request("$mBinary $mService $code $data")

                if (!response.startsWith("parcel:")) {
                    return false
                }

                if (reply != null) {
                    val raw = Base64.decode(response.substring(7), Base64.NO_WRAP)

                    reply.unmarshall(raw, 0, raw.size)
                    reply.setDataPosition(0)
                }

            } catch (e: Throwable) {
                return false

            } finally {
                if (!parcParsed) {
                    request.recycle()
                }
            }

            return true
        }
    }

    /**
     * @suppress
     */
    private fun request(msg: String): String {
        val reader = mStream.getReader()
        val cache = StringBuilder()
        val buffer = CharArray(64)

        mStream.writeLines(msg)

        while (true) {
            val len = reader.read(buffer)

            if (len > 0) {
                for (i in 0 until len) {
                    cache.append(buffer[i])
                }
            }

            if (len <= 0 || !reader.ready()) {
                reader.close(); break
            }
        }

        return cache.toString()
    }

    /**
     * @suppress
     */
    private fun install() {
        try {
            val sdkDirs = mContext.assets.list("Anycall")
            var sdk = 0

            for (entry in sdkDirs) {
                val num = entry.toInt()

                if (Build.VERSION.SDK_INT >= num && (num > sdk || sdk == 0)) {
                    sdk = num
                }
            }

            if (sdk == 0) {
                throw RuntimeException("Could not find binary matching this SDK/API")
            }

            val apiDirs = mContext.assets.list("Anycall/$sdk")
            var abi: String? = null

            for (entry in ABIS) {
                for (entry2 in apiDirs) {
                    if (entry.equals(entry2)) {
                        abi = entry; break
                    }
                }
            }

            if (abi == null) {
                throw RuntimeException("Could not find binary matching this Arch/ABI")
            }

            try {
                val localStream = BufferedReader(InputStreamReader(mContext.openFileInput("anycall.md5")))
                val assetStream = BufferedReader(InputStreamReader(mContext.assets.open("Anycall/$sdk/$abi/anycall.md5")))

                if ((localStream.readLine() ?: "").equals( assetStream.readLine() )) {
                    INSTALL = false
                    return
                }

                localStream.close()
                assetStream.close()

            } catch (e: FileNotFoundException) {
            }

            var localStream: BufferedOutputStream? = null
            var assetStream: BufferedInputStream? = null
            var files = arrayOf("anycall.md5", "anycall")
            val shell = Shell(mStream)

            try {
                for (file in files) {
                    localStream = BufferedOutputStream(mContext.openFileOutput("$file", 0))
                    assetStream = BufferedInputStream(mContext.assets.open("Anycall/$sdk/$abi/$file"))
                    val assetBuffer = ByteArray(1024)

                    while (true) {
                        val len = assetStream.read(assetBuffer)

                        if (len > 0) {
                            localStream.write(assetBuffer, 0, len)

                        } else {
                            break
                        }
                    }
                }

                if (!File(shell, mBinary).changeAccess(-1, -1, 755)) {
                    throw RuntimeException("It was not possible to install anycall")
                }

                INSTALL = false

            } catch (e: Throwable) {
                File(shell, mBinary).remove()
                File(shell, "$mBinary.md5").remove()

                throw RuntimeException("It was not possible to install anycall")

            } finally {
                localStream?.close()
                assetStream?.close()
                shell.close()
            }

        } catch (e: IOException) {
            throw RuntimeException(e.message, e)
        }
    }
}