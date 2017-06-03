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

/**
 * This class is used to get information about the device memory.
 *
 * It can provide information about total memory and swap space, free memory and swap etc.
 * It can also be used to check the device support for CompCache/ZRam and Swap along with
 * providing a list of active Swap and CompCache/ZRam devices.
 */
class Memory(shell: Shell) {

    private companion object {
        val REGEX_SPACE_SEARCH = Regex("[ \t]+")
    }

    /**
     *
     */
    data class MemoryInfo(
            /**
             * Total amount of memory in bytes, including SWAP space
             */
            val total: Long = 0L,

            /**
             * Free amount of memory in bytes, including SWAP space and cached memory
             */
            val free: Long = 0L,

            /**
             * Amount of cached memory including SWAP space
             */
            val cached: Long = 0L,

            /**
             * Amount of used memory including SWAP (Cached memory not included)
             */
            val used: Long = 0L,

            /**
             * Memory usage in percentage, including SWAP space (Cached memory not included)
             */
            val percentage: Int = 0,

            /**
             * Total amount of memory in bytes
             */
            val memTotal: Long = 0L,

            /**
             * Free amount of memory in bytes, including cached memory
             */
            val memFree: Long = 0L,

            /**
             * Amount of cached memory
             */
            val memCached: Long = 0L,

            /**
             * Amount of used memory (Cached memory not included)
             */
            val memUsed: Long = 0L,

            /**
             * Memory usage in percentage (Cached memory not included)
             */
            val memPercentage: Int = 0,

            /**
             * Total amount of SWAP space in bytes
             */
            val swapTotal: Long = 0L,

            /**
             * Free amount of SWAP space in bytes, including cached memory
             */
            val swapFree: Long = 0L,

            /**
             * Amount of cached SWAP space
             */
            val swapCached: Long = 0L,

            /**
             * Amount of used SWAP space (Cached memory not included)
             */
            val swapUsed: Long = 0L,

            /**
             * SWAP space usage in percentage (Cached memory not included)
             */
            val swapPercentage: Int = 0
    )

    /**
     *
     */
    data class SwapInfo(
            /**
             * Path to the SWAP device
             */
            val device: String = "",

            /**
             * SWAP size in bytes
             */
            val size: Long = 0L,

            /**
             * SWAP used in bytes
             */
            val used: Long = 0L,

            /**
             * A [Shell] instance used by [Swap]
             *
             * @suppress
             */
            protected val shell: Shell? = null
    ) {
        /**
         * Returns a [Swap] instance pointing to this Disk or 'NULL' if not possible
         */
        /*val disk: Disk? by lazy {
            if (shell != null) {
                Disk(shell, device)
            }

            null
        }*/
    }

    /** * */
    protected val mShell = shell

    /**
     * Get memory information like ram usage, ram total, cached memory, swap total etc.
     */
    fun getMemInfo(): MemoryInfo {
        val proc = File(mShell, "/proc/meminfo")
        proc.setForcedShell(true)

        val data = proc.read().getArray()
        var memTotal: Long = 0L
        var memFree: Long = 0L
        var memCached: Long = 0L
        var swapTotal: Long = 0L
        var swapFree: Long = 0L
        var swapCached: Long = 0L

        for (line in data) {
            val parts = line.split(REGEX_SPACE_SEARCH)

            when (parts[0]) {
                "MemTotal:" -> memTotal = parts[1].toLong() * 1024L
                "MemFree:" -> memFree = parts[1].toLong() * 1024L
                "Cached:" -> memCached = parts[1].toLong() * 1024L
                "SwapTotal:" -> swapTotal = parts[1].toLong() * 1024L
                "SwapFree:" -> swapFree = parts[1].toLong() * 1024L
                "SwapCached:" -> swapCached = parts[1].toLong() * 1024L
            }
        }

        return MemoryInfo(
                memTotal + swapTotal,
                (memFree + swapFree) + (memCached + swapCached),
                memCached + swapCached,
                (memTotal + swapTotal) - ((memFree + swapFree) + (memCached + swapCached)),
                (((memTotal + swapTotal) - ((memFree + swapFree) + (memCached + swapCached)) * 100L) / (memTotal + swapTotal)).toInt(),

                memTotal,
                memFree + memCached,
                memCached,
                memTotal - (memFree + memCached),
                (((memTotal - (memFree + memCached)) * 100L) / memTotal).toInt(),

                swapTotal,
                swapFree + swapCached,
                swapCached,
                swapTotal - (swapFree + swapCached),
                (((swapTotal - (swapFree + swapCached)) * 100L) / swapTotal).toInt()
        )
    }

    /**
     * Get a list of all active SWAP devices.
     */
    fun getSwapInfo(): Array<SwapInfo> {
        val proc = File(mShell, "/proc/swaps")
        proc.setForcedShell(true)

        if (proc.exists()) {
            val lines = proc.read().sort("/dev/").getArray()
            val list = mutableListOf<SwapInfo>()

            for (line in lines) {
                val parts = line.split(REGEX_SPACE_SEARCH)

                list.add(SwapInfo(
                        parts[0],
                        parts[2].toLong() * 1024L,
                        parts[3].toLong() * 1024L
                ))
            }

            return list.toTypedArray()
        }

        return arrayOf()
    }

    /**
     * Check whether or not CompCache/ZRam is supported by the kernel.
     *
     * This also checks for Swap support. If this returns FALSE, then none of them is supported.
     */
    fun hasCompCacheSupport(): Boolean {
        if (hasSwapSupport()) {
            val paths = arrayOf("/dev/block/ramzswap0", "/dev/block/zram0", "/system/lib/modules/ramzswap.ko", "/system/lib/modules/zram.ko", "/system/lib64/modules/ramzswap.ko", "/system/lib64/modules/zram.ko")

            for (path in paths) {
                if (File(mShell, path).exists()) {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Check whether or not Swap is supported by the kernel.
     */
    fun hasSwapSupport(): Boolean {
        return File(mShell, "/proc/swaps").exists()
    }

    /**
     * Change the swappiness level.
     *
     * The level should be between 0 for low swap usage and 100 for high swap usage.
     *
     * @param level
     *     The swappiness level
     */
    fun setSwappiness(level: Int): Boolean {
        val proc = File(mShell, "/proc/sys/vm/swappiness")
        proc.setForcedShell(true)

        if (proc.exists()) {
            return proc.write("$level")
        }

        return false
    }

    /**
     * Get the current swappiness level.
     */
    fun getSwappiness(): Int {
        val proc = File(mShell, "/proc/sys/vm/swappiness")
        proc.setForcedShell(true)

        if (proc.exists()) {
            return proc.read().getLine()?.toInt() ?: 0
        }

        return 0
    }
}