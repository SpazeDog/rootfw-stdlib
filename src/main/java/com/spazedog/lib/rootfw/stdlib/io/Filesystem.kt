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
import java.util.*

/**
 *
 */
class Filesystem(shell: Shell) {

    private companion object {
        val REGEX_SEPARATOR_SEARCH = Regex(",")
        val REGEX_SPACE_SEARCH = Regex("[ \t]+")
        val REGEX_PREFIX_SEARCH = Regex("^.*[A-Za-z]$")
    }

    /**
     *
     */
    data class DiskInfo(
            /**
             *
             */
            val device: String = "",

            /**
             *
             */
            val location: String = "",

            /**
             *
             */
            val fstype: String = "",

            /**
             *
             */
            val options: Array<String> = arrayOf<String>(),

            /**
             *
             */
            val size: Long = 0L,

            /**
             *
             */
            val used: Long = 0L,

            /**
             *
             */
            val available: Long = 0L,

            /**
             *
             */
            val percentage: Int = 0,

            /**
             * A [Shell] instance used by [Disk]
             *
             * @suppress
             */
            val shell: Shell? = null

    ) {
        /**
         * Returns a [Disk] instance pointing to this Disk or 'NULL' if not possible
         */
        val disk: Disk? by lazy {
            if (shell != null) {
                Disk(shell, device)
            }

            null
        }
    }

    /**
     *
     */
    data class MountInfo(
            /**
             *
             */
            val device: String = "",

            /**
             *
             */
            val location: String = "",

            /**
             *
             */
            val fstype: String = "",

            /**
             *
             */
            val options: Array<String> = arrayOf<String>(),

            /**
             * A [Shell] instance used by [Disk]
             *
             * @suppress
             */
            protected val shell: Shell? = null
    ) {
        /**
         * Returns a [Disk] instance pointing to this Disk or 'NULL' if not possible
         */
        val disk: Disk? by lazy {
            if (shell != null) {
                Disk(shell, device)
            }

            null
        }
    }

    /** * */
    protected val mShell = shell

    /**
     *
     */
    fun getMountInfo(): Array<MountInfo> {
        val proc = File(mShell, "/proc/mounts")
        proc.setForcedShell(true)

        val mounts = proc.read().trim().getArray()
        val lines = mutableListOf<MountInfo>()

        for (line in mounts) {
            val parts = line.split(REGEX_SPACE_SEARCH)

            if (parts.size == 4) {
                lines.add(MountInfo(
                        parts[0],
                        parts[1],
                        parts[2],
                        parts[3].split(REGEX_SEPARATOR_SEARCH).toTypedArray(),
                        mShell
                ))
            }
        }

        return lines.toTypedArray()
    }

    /**
     *
     */
    fun getDiskInfo(): Array<DiskInfo> {
        val proc = File(mShell, "/proc/mounts")
        proc.setForcedShell(true)

        val mounts = proc.read().trim().getArray()
        val lines = mutableListOf<DiskInfo>()

        for (line in mounts) {
            val parts = line.split(REGEX_SPACE_SEARCH)

            if (parts.size == 4) {
                val cmds = arrayOf(
                        "df -k '" + parts[1] + "'",
                        "df '" + parts[1] + "'"
                )

                for (cmd in cmds) {
                    val command = mShell.execute(cmd, 0, true)

                    if (command.getResultSuccess() && command.getSize() > 1) {
                        /* Depending on how long the line is, the df command some times breaks a line into two */
                        val dfparts = command.sort(1).trim().getString(" ").split(REGEX_SPACE_SEARCH)

                        /* Any 'df' output, no mater which toolbox or busybox version, should contain at least
                         'device or mount location', 'size', 'used' and 'available' */
                        if (dfparts.size > 3) {
                            var calc = DoubleArray(3)
                            var prefix: String
                            var prefixes = arrayOf("k", "m", "g", "t")
                            var percentage = 0

                            for (i in 1..3) {
                                val x = i-1

                                if (i < dfparts.size) {
                                    if (dfparts[i].matches(REGEX_PREFIX_SEARCH)) {
                                        calc[x] = dfparts[i].substring(0, dfparts[i].length-1).toDouble()
                                        prefix = dfparts[i].substring(dfparts[i].length-1).toLowerCase(Locale.US)

                                        for (listed in prefixes) {
                                            calc[x] *= 1024.0

                                            if (listed.equals(prefix)) {
                                                break
                                            }
                                        }

                                    } else {
                                        calc[x] = dfparts[i].toDouble() * 1024.0
                                    }

                                } else {
                                    calc[x] = 0.0
                                }
                            }

                            lines.add(DiskInfo(
                                    parts[0],
                                    parts[1],
                                    parts[2],
                                    parts[3].split(REGEX_SEPARATOR_SEARCH).toTypedArray(),
                                    calc[0].toLong(),
                                    calc[1].toLong(),
                                    calc[2].toLong(),
                                    if (calc[0] > 0) ((calc[1] * 100.0) / calc[0]).toInt() else 0,
                                    mShell
                            ))
                        }
                    }
                }
            }
        }

        return lines.toTypedArray()
    }
}