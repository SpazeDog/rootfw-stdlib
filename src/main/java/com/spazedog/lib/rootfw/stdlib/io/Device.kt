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

package com.spazedog.lib.rootfw.stdlib.io

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.spazedog.lib.rootfw.Shell
import com.spazedog.lib.rootfw.stdlib.android.SystemService

/**
 * Collect a list of processes running on the device or control the device power,
 * such as power off, reboot etc.
 */
open class Device(shell: Shell) {

    private companion object {
        val REGEX_PID_MATCH = Regex("^[0-9]+$")
        val REGEX_SPACE_SEARCH = Regex("[ \t]+")
    }

    /**
     * This is a container class used to store information about a process.
     */
    data class ProcessInfo(
            /**
             * The process path (Could be NULL) as not all processes has a path assigned
             */
            val path: String? = null,

            /**
             * The name of the process
             */
            val name: String = "",

            /**
             * The pid of the process
             */
            val pid: Int = -1,

            /**
             * A [Shell] instance used by [process]
             *
             * @suppress
             */
            private val shell: Shell? = null
    ) {
        /**
         * Returns a [Process] instance pointing to this process or 'NULL' if not possible
         */
        val process: Process? by lazy {
            if (shell != null && pid > 0) {
                Process(shell, name, pid)
            }

            null
        }
    }

    /** * */
    protected val mShell = shell

    /**
     * Return a list of all active processes
     *
     * @param pname
     *      Name of a process or `NULL` to include all. If name is provided,
     *      it will only include all active processes with that name.
     *      This can reduce a lot of overheat if you are only looking for a
     *      specific process. It's still going to have to scan all process files in /proc,
     *      but the provided name will limit the processing of the data in those files.
     */
    @JvmOverloads
    fun getProcessList(pname: String? = null): Array<ProcessInfo> {
        val procFile = File(mShell, "/proc")
        val procListing = procFile.getListing()

        if (procListing.size > 0) {
            val processes = mutableListOf<ProcessInfo>()
            var process: String? = null
            var path: String? = null

            for (name in procListing) {
                if (name.matches(REGEX_PID_MATCH)) {
                    process = File(mShell, "/proc/$name/cmdline").readOneLine()

                    if (process == null) {
                        process = File(mShell, "/proc/$name/stat").readOneLine()

                        try {
                            if (pname == null || process?.contains(pname) ?: false) {
                                process = process!!.trim().split(REGEX_SPACE_SEARCH)[1] // Process name with ()
                                process = process.substring(1, process.length-1) // Remove ( and )

                            } else {
                                continue
                            }

                        } catch (e: Throwable) {
                            continue
                        }

                    } else if (pname == null || process!!.contains(pname)) {
                        if (process.contains('/')) {
                            /*
                             * Some times we simply get 'process' from 'cmdline',
                             * other times we get '/path/to/process' and some times '[/path/to]process-arguments
                             * or 'process/path/to/'
                             *
                             * We want to split 'process' and '/path/to' and descard anything else.
                             */
                            path = process.substring(process.indexOf('/'),
                                    if (process.contains('-')) {
                                        process.indexOf('-', process.lastIndexOf('/', process.indexOf('/')))

                                    } else {
                                        process.length
                                    }
                            )

                            if (!process.startsWith("/")) {
                                /*
                                 * 'process/path/to/'
                                 */
                                process = process.substring(0, process.indexOf("/"))

                            } else {
                                /*
                                 * '/path/to/process[-arguments]
                                 */
                                process = process.substring(
                                        process.lastIndexOf('/',
                                                if (process.contains("-")) {
                                                    process.indexOf("-")

                                                } else {
                                                    process.length
                                                }
                                        )+1,

                                        if (process.contains("-")) {
                                            process.indexOf("-", process.lastIndexOf("/", process.indexOf("-")))

                                        } else {
                                            process.length
                                        }
                                )
                            }

                        } else if (process.contains('-')) {
                            /*
                             * 'process-arguments
                             */
                            process = process.substring(0, process.indexOf("-"));
                        }

                    } else {
                        continue
                    }

                    if (process != null && (pname == null || process.equals(pname))) {
                        processes.add(
                                ProcessInfo(path, process!!, name.toInt(), mShell)
                        )
                    }
                }
            }

            return processes.toTypedArray()
        }

        return arrayOf()
    }

    /**
     * Reboots the device into the recovery
     *
     * Note that not every toolbox/reboot binaries support recovery reboot
     */
    @JvmOverloads
    fun rebootRecovery(context: Context? = null): Boolean {
        if (context != null) {
            try {
                // Let's try calling Androids internal API first
                val service = SystemService(context, mShell.getStream(), Context.POWER_SERVICE, "android.os.IPowerManager")

                if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD) {
                    service.invoke("reboot", null, false, "recovery", false)

                } else {
                    service.invoke("reboot", null, "")
                }

                service.close()

            } catch (e: Throwable) {
            }
        }

        // Either it failed, or a Context was not provided
        return mShell.execute("reboot recovery").getResultSuccess()
    }

    /**
     * Reboots the device
     *
     * Uses sysrq trigger if toolbox/reboot fails
     */
    @JvmOverloads
    fun reboot(context: Context? = null): Boolean {
        if (context != null) {
            try {
                // Let's try calling Androids internal API first
                val service = SystemService(context, mShell.getStream(), Context.POWER_SERVICE, "android.os.IPowerManager")

                if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD) {
                    service.invoke("reboot", null, false, "", false)

                } else {
                    service.invoke("reboot", null, "")
                }

                service.close()

            } catch (e: Throwable) {
            }
        }

        if(!mShell.execute("reboot").getResultSuccess()) {
            return mShell.execute("echo 1 > /proc/sys/kernel/sysrq && echo s > /proc/sysrq-trigger && echo b > /proc/sysrq-trigger").getResultSuccess()
        }

        return true
    }

    /**
     * Makes a soft reboot of the device
     */
    @JvmOverloads
    fun rebootSoft(context: Context? = null): Boolean {
        if (context != null) {
            try {
                // Let's try calling Androids internal API first
                val service = SystemService(context, mShell.getStream(), Context.POWER_SERVICE, "android.os.IPowerManager")

                service.invoke("crash", null, "Soft Reboot")
                service.close()

            } catch (e: Throwable) {
            }
        }

        // Try stopping and starting the entire Android framework
        if (!mShell.execute("stop && start").getResultSuccess()) {
            // Fallback to trying sysrq trigger
            return mShell.execute("echo 1 > /proc/sys/kernel/sysrq && echo s > /proc/sysrq-trigger && echo e > /proc/sysrq-trigger").getResultSuccess()
        }

        return true
    }

    /**
     * Powers down the device
     */
    @JvmOverloads
    fun shutdown(context: Context? = null): Boolean {
        if (context != null && VERSION.SDK_INT > VERSION_CODES.GINGERBREAD) {
            try {
                // Let's try calling Androids internal API first
                val service = SystemService(context, mShell.getStream(), Context.POWER_SERVICE, "android.os.IPowerManager")

                service.invoke("shutdown", null, false, false)
                service.close()

            } catch (e: Throwable) {
            }
        }

        if(!mShell.execute("reboot -p").getResultSuccess()) {
            return mShell.execute("echo 1 > /proc/sys/kernel/sysrq && echo s > /proc/sysrq-trigger && echo o > /proc/sysrq-trigger").getResultSuccess()
        }

        return true
    }
}