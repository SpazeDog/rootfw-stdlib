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
import com.spazedog.lib.rootfw.Command.Containers.Call
import com.spazedog.lib.rootfw.Shell

/**
 * Special extension to [Swap] that handles CompCache/ZRam
 *
 * This one is able to detect the correct device and load any missing modules.
 * This should always be used on CompCache/ZRam rather than [Swap] directly.
 */
class CompCache(shell: Shell) : Swap(shell, loadDevice(shell)) {

    /**
     *
     */
    private companion object {
        /**
         * @suppress
         */
        fun loadDevice(shell: Shell): String {
            val blks = arrayOf("/dev/block/zram0", "/dev/block/ramzswap0")
            val libs = arrayOf(
                    arrayOf("/system/lib64/modules/zram.ko", "/system/lib/modules/zram.ko"),
                    arrayOf("/system/lib64/modules/ramzswap.ko", "/system/lib/modules/ramzswap.ko")
            )

            for (i in 0..1) {
                if (File(shell, blks[i]).isBlockDevice()) {
                    return blks[i]

                } else {
                    for (lib in libs[i]) {
                        if (File(shell, lib).isBlockDevice()) {
                            shell.execute("insmod '$lib'", 0, true)
                        }

                        return blks[i]
                    }
                }
            }

            return "/dev/block/zram0"
        }
    }

    /**
     * Enable this Swap device
     *
     * @param priority
     *      Priority over other swap devices
     */
    override fun setSwapOn(priority: Int): Boolean = setSwapOn(priority, 18)

    /**
     * Enable this Swap device
     *
     * @param priority
     *      Priority over other swap devices
     *
     * @param size
     *      Size in percentage of total device memory to use.
     *      Default is 18% and it should no go beyond 35%
     */
    fun setSwapOn(priority: Int = 0, size: Int = 18): Boolean {
        if (!isActive()) {
            val memInfo = mMemory.getMemInfo()
            var status = false

            if (mDevice.getName() == "zram0") {
                val command = Command {
                    Call(
                            "echo 1 > /sys/block/zram0/reset && " +
                            "echo '" + ((memInfo.memTotal * size) / 100) + "' > /sys/block/zram0/disksize && " +
                            "$it mkswap '" + getPath() + "'"
                    )
                }

                status = mShell.execute(command).getResultSuccess()

            } else {
                status = mShell.execute("rzscontrol '" + getPath() + "' --disksize_kb='" + (((memInfo.memTotal * size) / 100) * 1024) + "' --init").getResultSuccess()
            }

            if (status) {
                super.setSwapOn(priority)
            }
        }

        return true
    }

    /**
     * Disable this Swap device.
     */
    override fun setSwapOff(): Boolean {
        if (isActive()) {
            if (super.setSwapOff()) {
                if (mDevice.getName() == "zram0") {
                    return mShell.execute("echo 1 > /sys/block/zram0/reset").getResultSuccess()

                } else {
                    return mShell.execute("rzscontrol '" + getPath() + "' --reset").getResultSuccess()
                }
            }

            return false
        }

        return true
    }
}