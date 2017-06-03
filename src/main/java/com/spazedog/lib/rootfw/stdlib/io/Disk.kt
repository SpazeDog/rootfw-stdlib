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
import com.spazedog.lib.rootfw.stdlib.io.Filesystem.DiskInfo
import com.spazedog.lib.rootfw.stdlib.io.Filesystem.MountInfo

/**
 * Used to handle device files
 */
class Disk(shell: Shell, disk: String) {

    /** * */
    private val mDisk = File(shell, disk)

    /** * */
    private val mShell = shell

    /** * */
    private val mFilesystem: Filesystem by lazy {
        Filesystem(mShell)
    }

    /**
     * Remount a currently mounted device with altered options.
     *
     * For example `rw` to remount a device with read/write option
     */
    fun remount(vararg options: String): Boolean = mountInternal(null, null, *options)

    /**
     * Mount this device or folder to a specified location.
     *
     * If this device is a folder, it will create a `bind` mount to the location.
     *
     * @param location
     *      Mount location
     *
     * @param fstype
     *      Optional file system type, or `NULL` for `auto`
     *
     * @param options
     *      One or more mount options
     */
    @JvmOverloads
    fun mount(location: String, fstype: String? = null, vararg options: String): Boolean = mountInternal(location, fstype, *options)

    /**
     * @suppress
     */
    private fun mountInternal(location: String? = null, fstype: String? = null, vararg options: String): Boolean {
        val cmd = if (location != null && mDisk.isDirectory()) {
            "mount --bind '" + mDisk.getAbsolutePath() + "' '" + File(mShell, location).getAbsolutePath() + "'"

        } else {
            var cmd = "mount"

            if (fstype != null) {
                cmd += " -t '$fstype'"
            }

            if (options.size > 0) {
                cmd += " -o "

                if (location == null) {
                    cmd += "remount,"
                }

                val buff = StringBuilder();
                for (option in options) {
                    if (buff.length > 0) {
                        buff.append(",")
                    }

                    buff.append(option)
                }

                cmd += buff.toString()
            }

            cmd += " '" + mDisk.getAbsolutePath() + "'"

            if (location != null) {
                cmd += " '" + File(mShell, location).getAbsolutePath() + "'"
            }

            cmd
        }

        /*
         * On some devices, some partitions has been made read-only by writing to the block device ioctls.
         * This means that even mounting them as read/write will not work by itself, we need to change the ioctls as well.
         */
        if (options.size > 0 && !"/".equals(mDisk.getAbsolutePath())) {
            for (option in options) {
                if (option.equals("rw")) {
                    var blkdev: String? = if (mDisk.isDirectory()) {
                        getMountInfo()?.device

                    } else {
                        mDisk.getAbsolutePath()
                    }

                    if (blkdev != null && (blkdev.startsWith("/dev/") || blkdev.startsWith("/sys/"))) {
                        mShell.execute("blockdev --setrw '" + blkdev + "' 2> /dev/null", 0, true)
                    }

                    break
                }
            }
        }

        return mShell.execute(cmd, 0, true).getResultSuccess()
    }

    /**
     * This method is used to remove an attachment of a device or folder (unmount).
     */
    fun unmount(): Boolean {
        val cmds = arrayOf(
                "umount '" + mDisk.getAbsolutePath() + "'",
                "umount -f '" + mDisk.getAbsolutePath() + "'"
        )

        for (cmd in cmds) {
            if (mShell.execute(cmd, 0, true).getResultSuccess()) {
                return true
            }
        }

        return false
    }

    /**
     * This is used to check whether the current device or folder is attached to a location (Mounted).
     */
    fun isMounted(): Boolean {
        return getMountInfo() != null
    }

    /**
     * This method is used to move a mount location to another location.
     */
    fun move(dest: String): Boolean {
        /*
         * Not all toolbox versions support moving mount points.
         * So in these cases, we fallback to a manual unmount/remount.
         */
        if (!mShell.execute("mount --move '" + mDisk.getAbsolutePath() + "' '" + File(mShell, dest).getAbsolutePath() + "'", 0, true).getResultSuccess()) {
            val info = getMountInfo()

            if (info != null && unmount()) {
                return Disk(mShell, info.device).mount(info.location, info.fstype, *info.options)
            }

            return false
        }

        return true
    }

    /**
     * Check if this device was mounted with a specific mount option
     */
    fun hasOption(option: String): Boolean {
        return getOption(option) != null
    }

    /**
     * This can be used to get the value of a specific mount option that was used to attach the file system.
     *
     * Note that options like <code>noexec</code>, <code>nosuid</code> and <code>nodev</code> does not have any values and will return themselves as value.
     * This method is used to get values from options like <code>gid=xxxx</code>, <code>mode=xxxx</code> and <code>size=xxxx</code> where <code>xxxx</code> is the value.
     *
     * @param option
     *     The name of the option to find
     *
     * @return
     *     The option value or `NULL` if device is not mounted
     */
    fun getOption(option: String): String? {
        val info = getMountInfo()

        if (info != null) {
            for (option in info.options) {
                if (option.startsWith("$option=")) {
                    return option.substring(option.indexOf('=')+1)

                } else if (option.equals(option)) {
                    return option
                }
            }
        }

        return null
    }

    /**
     * Get mount information about this disk
     *
     * This includes device path, mount location, file system type and mount options
     */
    fun getMountInfo(): MountInfo? {
        val infos = mFilesystem.getMountInfo()
        val isDir = mDisk.isDirectory()
        var path = mDisk.getCanonicalPath()

        do {
            for (info in infos) {
                if ((!isDir && info.device.equals(path)) || (isDir && info.location.equals(path))) {
                    return info
                }
            }

        } while (isDir && path.lastIndexOf("/") > 0 && !{path = path.substring(0, path.lastIndexOf("/")); path}().equals(""))

        return null
    }

    /**
     * Extended version of [getMountInfo]
     *
     * This one includes disk size, used and remaining space
     */
    fun getDiskInfo(): DiskInfo? {
        val infos = mFilesystem.getDiskInfo()
        val isDir = mDisk.isDirectory()
        var path = mDisk.getCanonicalPath()

        do {
            for (info in infos) {
                if ((!isDir && info.device.equals(path)) || (isDir && info.location.equals(path))) {
                    return info
                }
            }

        } while (isDir && path.lastIndexOf("/") > 0 && !{path = path.substring(0, path.lastIndexOf("/")); path}().equals(""))

        return null
    }
}