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
 * THE SOFTWARE IS PROVIDED "AS IS" to WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.spazedog.lib.rootfw.stdlib.io

import com.spazedog.lib.rootfw.Command
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.File as JavaFile
import java.io.Writer as JavaWriter
import java.lang.Process as Process

/**
 * This class is used to write to a file. Unlike [java.io.FileWriter], this class
 * will fallback on a SuperUser stream whenever a write action is not allowed by the application.
 */
class FileWriter @JvmOverloads constructor(path: String, append: Boolean = false, forcedShell: Boolean = false) : JavaWriter() {

    /** * */
    private var mStream: OutputStreamWriter? = null

    /** * */
    private var mProcess: Process? = null

    /** * */
    private val mForcedShell = forcedShell

    /**
     *
     */
    init {
        if (!mForcedShell) {
            try {
                mStream = OutputStreamWriter(FileOutputStream(path, append))

            } catch (e: Throwable) {
                mStream = null
            }
        }

        if (mStream == null) {
            val absPath = JavaFile(path).absolutePath
            val redirect = if (append) ">>" else ">"
            val command = Command("cat $redirect '$absPath' || exit 1\n", 130, true)

            outer@
            while (true) {
                for (call in command.getCalls()) {
                    try {
                        mProcess = ProcessBuilder("su").start()
                        mStream = OutputStreamWriter(mProcess!!.outputStream)
                        mStream!!.write(call.command)
                        mStream!!.flush()

                        try {
                            synchronized(mStream!!) {
                                /*
                             * The only way to check for errors, is by giving the shell a bit of time to fail.
                             * This can either be an error caused by a missing binary for 'cat', or caused by something
                             * like writing to a read-only fileystem.
                             */
                                (mStream as java.lang.Object).wait(100)
                            }

                        } catch (ignore: Throwable) {
                        }

                        try {
                            /*
                         * If the process is active, it will throw IllegalThreadStateException
                         */
                            mProcess!!.exitValue()
                            throw IOException()

                        } catch (ignore: IllegalThreadStateException) {
                            break@outer
                        }

                    } catch (te: Throwable) {
                        mProcess?.destroy()
                    }
                }

                throw IOException("It was not possible to open file 'path' for writing")
            }
        }
    }

    /**
     *
     */
    @JvmOverloads
    constructor(file: JavaFile, append: Boolean = false, forcedShell: Boolean = false) : this(file.absolutePath, append, forcedShell)

    /**
     *
     */
    @JvmOverloads
    constructor(file: File, append: Boolean = false, forcedShell: Boolean = false) : this(file.getAbsolutePath(), append, forcedShell)

    /**
     *
     */
    override fun write(buf: CharArray?, offset: Int, len: Int) {
        synchronized(this) {
            mStream?.write(buf, offset, len)
        }
    }

    /**
     *
     */
    override fun flush() {
        synchronized(this) {
            mStream?.flush()
        }
    }

    /**
     *
     */
    override fun close() {
        synchronized(this) {
            flush()

            mStream?.close();
            mProcess?.destroy();
        }
    }
}