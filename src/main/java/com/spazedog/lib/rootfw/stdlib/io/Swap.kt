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

import com.spazedog.lib.rootfw.Shell
import com.spazedog.lib.rootfw.stdlib.io.Memory.SwapInfo

/**
 * Handle a specific swap device
 */
open class Swap(shell: Shell, device: String) {

    /** * */
    protected val mShell = shell

    /** * */
    protected val mDevice = File(mShell, device)

    /** * */
    protected val mMemory: Memory by lazy {
        Memory(mShell)
    }

    /**
     * Check whether or not this block device exists
     */
    fun exists(): Boolean = mDevice.exists()

    /**
     * Get the path to the Swap block device
     */
    fun getPath(): String = mDevice.getResolvedPath()

    /**
     * Check whether or not this Swap device is currently active
     */
    fun isActive(): Boolean = getSwapInfo() != null

    /**
     * Get information about this particular swap device, it needs to be active.
     */
    fun getSwapInfo(): SwapInfo? {
        val infos = mMemory.getSwapInfo()
        val path = mDevice.getResolvedPath()

        for (info in infos) {
            if (info.device.equals(path)) {
                return info
            }
        }

        return null
    }

    /**
     * Enable this Swap device
     *
     * @param priority
     *      Priority over other swap devices
     */
    @JvmOverloads
    open fun setSwapOn(priority: Int = 0): Boolean {
        if (!isActive()) {
            val cmds = if (priority > 0) {
                arrayOf<String>(
                        "swapon -p '" + priority + "' '" + getPath() + "'",
                        "swapon '" + getPath() + "'"
                )

            } else {
                arrayOf<String>("swapon '" + getPath() + "'")
            }

            for (cmd in cmds) {
                if (mShell.execute(cmd, 0, true).getResultSuccess()) {
                    return true
                }
            }

            return false
        }

        return true
    }

    /**
     * Disable this Swap device.
     */
    open fun setSwapOff(): Boolean {
        if (isActive()) {
            return mShell.execute("swapoff '" + getPath() + "'", 0, true).getResultSuccess()
        }

        return true
    }
}