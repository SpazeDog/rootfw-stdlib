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
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.File as JavaFile
import java.io.Reader as JavaReader
import java.lang.Process as Process

/**
 * This class is used to read from a file. Unlike [java.io.FileReader], this class
 * will fallback on a SuperUser stream whenever a read action is not allowed by the application.
 */
class FileReader @JvmOverloads constructor(path: String, forcedShell: Boolean = false) : JavaReader() {

    /** * */
    private var mInputStream: InputStreamReader? = null

    /** * */
    private var mOutputStream: OutputStreamWriter? = null

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
                mInputStream = InputStreamReader(FileInputStream(path))

            } catch (e: Throwable) {
                mInputStream = null
            }
        }

        if (mInputStream == null) {
            val absPath = JavaFile(path).absolutePath
            val command = Command("cat '$absPath' || exit 1\n", 0, true)

            outer@
            while (true) {
                for (call in command.getCalls()) {
                    try {
                        mProcess = ProcessBuilder("su").start()
                        mOutputStream = OutputStreamWriter(mProcess!!.outputStream)
                        mOutputStream!!.write(call.command)
                        mOutputStream!!.flush()

                        try {
                            synchronized(mOutputStream!!) {
                                /*
                             * The only way to check for errors, is by giving the shell a bit of time to fail.
                             * This can either be an error caused by a missing binary for 'cat', or caused by something
                             * like writing to a read-only fileystem.
                             */
                                (mOutputStream as java.lang.Object).wait(100)
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
                            /*
                             * No errors, let's get the input stream
                             */
                            mInputStream = InputStreamReader(mProcess!!.inputStream)

                            /*
                             * Make sure that the process quits when all data has been read.
                             */
                            mOutputStream!!.write("exit $?\n")
                            mOutputStream!!.flush()

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
    constructor(file: JavaFile, forcedShell: Boolean = false) : this(file.absolutePath, forcedShell)

    /**
     *
     */
    @JvmOverloads
    constructor(file: File, forcedShell: Boolean = false) : this(file.getAbsolutePath(), forcedShell)

    /**
     *
     */
    override fun mark(readLimit: Int) {
        mInputStream?.mark(readLimit)
    }

    /**
     *
     */
    override fun markSupported(): Boolean {
        return mInputStream?.markSupported() ?: false
    }

    /**
     *
     */
    override fun close() {
        mOutputStream?.close()
        mInputStream?.close()
        mProcess?.destroy()
    }

    /**
     *
     */
    override fun read(buffer: CharArray, offset: Int, count: Int): Int {
        return mInputStream?.read(buffer, offset, count) ?: 0
    }

    /**
     *
     */
    override fun skip(charCount: Long): Long {
        return mInputStream?.skip(charCount) ?: 0L
    }

    /**
     *
     */
    override fun reset() {
        mInputStream?.reset()
    }

    /**
     *
     */
    override fun ready(): Boolean {
        return mInputStream != null
    }
}