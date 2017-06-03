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

import com.spazedog.lib.rootfw.Command
import com.spazedog.lib.rootfw.Shell

/**
 * Handle a single specific process running on the device
 */
class Process internal constructor(shell: Shell, pname: String? = null, pid: Int = 0) {

    private companion object {
        val REGEX_PID_MATCH = Regex("^[0-9]+$")
        val REGEX_SPACE_SEARCH = Regex("[ \t]+")
    }

    /** * */
    private val mShell = shell

    /** * */
    private var mPid = 0

    /** * */
    private var mPName: String? = null

    /** * */
    private val mDevice: Device by lazy {
        Device(mShell)
    }

    /**
     * @suppress
     */
    init {
        if (pname?.matches(REGEX_PID_MATCH) ?: false) {
            mPid = pname!!.toInt()

        } else {
            mPid = pid
            mPName = pname
        }
    }

    /**
     * @param shell
     *      An instance of [Shell]
     *
     * @param pid
     *      Pid of a process
     */
    constructor(shell: Shell, pid: Int) : this(shell, null, pid)

    /**
     * @param shell
     *      An instance of [Shell]
     *
     * @param pname
     *      Name of a process
     */
    constructor(shell: Shell, pname: String) : this(shell, pname, 0)

    /**
     * Get the pid of the current process.
     *
     * If you initialized this object using a pid, this method will return that.
     * Otherwise it will return the first found pid for this process name.
     */
    fun getPid(): Int {
        if (mPid > 0) {
            return mPid
        }

        val bin = Command.getBinary(mShell, "pidof")

        if (bin != null) {
            val pids = mShell.execute("$bin '$mPName'").getLine()

            if (pids != null) {
                try {
                    return pids.trim().split(REGEX_SPACE_SEARCH)[0].toInt()

                } catch (e: Throwable) {
                }
            }

        } else {
            val processes = mDevice.getProcessList(mPName)

            if (processes.size > 0) {
                return processes.get(0).pid
            }
        }

        return 0
    }

    /**
     * Get a list of all pid's for this process name.
     */
    fun getPids(): IntArray {
        val bin = Command.getBinary(mShell, "pidof")
        val name = getName()

        if (bin != null) {
            val pids = mShell.execute("$bin '$name'").getLine()

            if (pids != null) {
                val parts = pids.trim().split(REGEX_SPACE_SEARCH)
                val values = IntArray(parts.size)

                for (i in 0 until parts.size) {
                    values[i] = parts[i].toInt()
                }

                return values
            }

        } else {
            val processes = mDevice.getProcessList(name)
            val values = IntArray(processes.size)

            for (i in 0 until processes.size) {
                values[i] = processes[i].pid
            }

            return values
        }

        return intArrayOf()
    }

    /**
     * Get the process name of the current process
     *
     * If you initialized this object using a process name, this method will return that name.
     * Otherwise it will locate it in /proc based on the pid.
     */
    fun getName(): String? {
        if (mPName != null) {
            return mPName
        }

        var process = File(mShell, "/proc/$mPid/cmdline").readOneLine()

        if (process == null) {
            process = File(mShell, "/proc/$mPid/stat").readOneLine()

            try {
                process = process!!.trim().split(REGEX_SPACE_SEARCH)[1] // Process name with ()
                process = process.substring(1, process.length-1) // Remove ( and )

                mPName = process

            } catch (e: Throwable) {
            }

        } else {
            if (process.contains('/')) {
                /*
                 * Some times we simply get 'process' from 'cmdline',
                 * other times we get '/path/to/process' and some times '[/path/to]process-arguments
                 * or 'process/path/to/'
                 *
                 * We want to split 'process' and '/path/to' and descard anything else.
                 */
                if (!process.startsWith("/")) {
                    /*
                     * 'process/path/to/'
                     */
                    mPName = process.substring(0, process.indexOf("/"))

                } else {
                    /*
                     * '/path/to/process[-arguments]
                     */
                    mPName = process.substring(
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
                mPName = process.substring(0, process.indexOf("-"));
            }

        }

        return mPName
    }

    /**
     * Kill this process
     *
     * If you initialized this object using a pid, only this single process will be killed.
     * If you used a process name, all processes with this process name will be killed.
     */
    fun kill(): Boolean {
        if (mPid > 0) {
            return mShell.execute("kill -9 '$mPid'", 0, true).getResultSuccess()

        } else {
            val command = mShell.execute("killall '$mPName'", 0, true)

            /* Toolbox does not support 'killall' */
            if (!command.getResultSuccess()) {
                val pids = getPids()

                for (pid in pids) {
                    if (!mShell.execute("kill -9 '$pid'", 0, true).getResultSuccess()) {
                        return false
                    }
                }
            }

            return true
        }
    }
}