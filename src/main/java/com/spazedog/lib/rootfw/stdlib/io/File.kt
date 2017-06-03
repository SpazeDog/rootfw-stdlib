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
import com.spazedog.lib.rootfw.Command
import com.spazedog.lib.rootfw.Command.Containers.Call
import com.spazedog.lib.rootfw.Shell
import com.spazedog.lib.rootfw.stdlib.io.File.Type
import com.spazedog.lib.rootfw.stdlib.utils.convertToPermissions
import com.spazedog.lib.rootfw.utils.Data
import com.spazedog.lib.rootfw.stdlib.utils.convertToUID
import java.io.*

import java.io.File as JavaFile
import java.io.FileReader as JavaFileReader
import java.io.FileWriter as JavaFileWriter


/**
 * Extended version of `java.io.File` (Not a member) with additional tools
 * and ability to use `root` as fallback to force access to any file/directory
 */
class File(shell: Shell, file: JavaFile) {

    private companion object {
        val REGEX_DOT_SEARCH = Regex("^\\.")
        val REGEX_COLUMN_SEARCH = Regex("[ ]{2,}")
        val REGEX_SPACE_SEARCH = Regex("[ \t]+")
        val REGEX_STAT_SEARCH = Regex("^([a-z-]+)(?:[ \t]+([0-9]+))?[ \t]+([0-9a-z_]+)[ \t]+([0-9a-z_]+)(?:[ \t]+(?:([0-9]+),[ \t]+)?([0-9]+))?[ \t]+([A-Za-z]+[ \t]+[0-9]+[ \t]+[0-9:]+|[0-9-/]+[ \t]+[0-9:]+)[ \t]+(?:(.*) -> )?(.*)$")
        val REGEX_STAT_SPLIT = Regex("\\|")
    }

    /**
     *
     */
    constructor(shell: Shell, filePath: String): this(shell, JavaFile(filePath))

    /**
     *
     */
    class FileData(lines: Array<String> = arrayOf()) : Data<FileData>(lines)

    /**
     *
     */
    data class FileStat(
            val name: String,
            val link: String? = null,
            val type: Type = Type.None,
            val user: Int = -1,
            val group: Int = -1,
            val access: String = "",
            val permissions: Int = 0,
            val mm: String? = null,
            val size: Long = 0L
    )

    /** * */
    enum class Type(val char: Char) { File('f'), Directory('d'), Link('L'), Block('b'), Character('c'), Any('e'), None('n') }

    /** * */
    private val mShell = shell

    /** * */
    private val mFile = file

    /** * */
    private var mForceShell = false

    /**
     * Check whether or not this file is of the type [Type], if it exists
     *
     * @param type
     *      The type to match against
     */
    fun isType(type: Type): Boolean {
        when (type) {
            Type.File -> if (try { mFile.isFile() } catch (e: Throwable) { false }) {
                return true
            }

            Type.Directory -> if (try { mFile.isDirectory() } catch (e: Throwable) { false }) {
                return true
            }

            Type.Any -> if (try { mFile.exists() } catch (e: Throwable) { false }) {
                return true
            }
        }

        var command = Command {
            if (type != Type.None) {
                Call("( ${if (it == null) "" else it} test -${type.char} '" + getAbsolutePath() + "' && echo true ) || ( ${if (it == null) "" else it} test ! -${type.char} '" + getAbsolutePath() + "' && echo false )", arrayOf(0))

            } else {
                Call("( ${if (it == null) "" else it} test ! -e '" + getAbsolutePath() + "' && echo true ) || ( ${if (it == null) "" else it} test -e '" + getAbsolutePath() + "' && echo false )", arrayOf(0))
            }
        }

        mShell.execute(command)

        if (command.getResultSuccess()) {
            return "true".equals(command.getLine())

        /*
         * Some toolsbox version does not have the 'test' command.
         * Instead we use alternative methods. Most of the time it will not be used,
         * since most devices has busbox or toybox
         */
        } else if (type == Type.Any || type == Type.None) {
            command = Command("ls '" + getAbsolutePath() + "' > /dev/null 2>&1", 0, true)

            mShell.execute(command)

            return ( command.getResultSuccess() && type == Type.Any ) || ( !command.getResultSuccess() && type == Type.None )

        } else {
            val stat = getDetails()

            if (type != Type.Link && stat.type == Type.Link) {
                return getCanonicalFile().isType(type)
            }

            return type == stat.type
        }

        return false
    }

    /**
     * Force the shell to be used on all operations
     *
     * By default, this class tries to use the Java API's for all file operations.
     * It only uses shell access/root whenever we don't have access to a particular file.
     * But some files must be handled by the shell in newer Android releases, one example being
     * reading from `/proc/mounts`, because even though applications has access to this file,
     * they don't have access to the original version. A root process however does.
     *
     * @param flag
     *      Whether or not to force operations through the shell
     */
    fun setForcedShell(flag: Boolean) {
        mForceShell = flag
    }

    /**
     * Check whether or not this file is a directory, if it exists
     */
    fun isDirectory(): Boolean = isType(Type.Directory)

    /**
     * Check whether or not this file is an actual file, if it exists
     */
    fun isFile(): Boolean = isType(Type.File)

    /**
     * Check whether or not this file is a symbolic link, if it exists
     */
    fun isLink(): Boolean = isType(Type.Link)

    /**
     * Check whether or not this file is a block device, if it exists
     */
    fun isBlockDevice(): Boolean = isType(Type.Block)

    /**
     * Check whether or not this file is a character device, if it exists
     */
    fun isCharacterDevice(): Boolean = isType(Type.Character)

    /**
     * Check whether or not a file exists
     */
    fun exists(): Boolean = isType(Type.Any)

    /**
     * Returns the absolute path. An absolute path is a path that starts at a root of the file system
     */
    fun getAbsolutePath(): String = mFile.absolutePath

    /**
     * Returns the path used to create this object
     */
    fun getPath(): String = mFile.path

    /**
     * Returns the parent path. Note that on folders, this means the parent folder.
     * However, on files, it will return the folder path that the file resides in.
     */
    fun getParentPath(): String {
        if (mFile.parent != null) {
            return mFile.parent
        }

        val path = getResolvedPath()

        if (path == "/") {
            return path
        }

        return path.substring(0, path.lastIndexOf('/'))
    }

    /**
     * Get a real absolute path
     *
     * Java's <code>getAbsolutePath</code> is not a fully resolved path. Something like <code>./file</code> could be returned as <code>/folder/folder2/.././file</code> or simular.
     * This method however will resolve a path and return a fully absolute path <code>/folder/file</code>. It is a bit slower, so only use it when this is a must.
     */
    fun getResolvedPath(): String {
        var path = getAbsolutePath()

        if (path.contains("")) {
            val directories = (if (path == "/") path else if (path.endsWith("/")) path.substring(1, path.length - 1) else path.substring(1)).split("/");
            val resolved = mutableListOf<String>()

            for (dir in directories) {
                if (dir.equals("generated/source/r")) {
                    if (resolved.size > 0) {
                        resolved.removeAt( resolved.size -1 )
                    }

                } else if (!dir.equals("")) {
                    resolved.add(dir)
                }
            }

            path = if (resolved.size > 0) {
                val builder = StringBuilder()

                for (res in resolved) {
                    builder.append("/$res")
                }

                builder.toString()

            } else {
                "/"
            }
        }

        return path
    }

    /**
     * Resolve the path if this is a link and return the path of the original file
     */
    fun getCanonicalPath(): String {
        /*
         * First let's try the JVM way
         */
        try {
            return mFile.canonicalPath

        } catch (e: Throwable) {}

        /*
         * Second we try using `readlink`, if available
         */
        val command = Command("readlink -f '" + getAbsolutePath() + "' 2> /dev/null", 0, true)

        mShell.execute(command)

        if (command.getResultSuccess() && command.getSize() > 0) {
            // This will never be NULL if `size` > 0
            return command.getLine()!!
        }

        /*
         * And third we fallback to a slower but affective method
         */
        var stat = getDetails()

        if (stat.link != null) {
            var realPath: String = stat.link!!

            while ({stat = newFile(realPath).getDetails(); stat.type}() != Type.None && stat.link != null) {
                realPath = stat.link!!
            }

            return realPath
        }

        // Not a link
        return getAbsolutePath()
    }

    /**
     * Create a new instance of this class that points to a different file
     *
     * @param path
     *      Path to the new file
     */
    fun newFile(path: String): File = newFile(JavaFile(path))

    /**
     * Create a new instance of this class that points to a different file
     *
     * @param file
     *      A [java.io.File] instance
     */
    fun newFile(file: JavaFile): File = File(mShell, file)

    /**
     * Return a new [File] object using the parent path
     */
    fun getParentFile(): File = File(mShell, getParentPath())

    /**
     * Return a new [File] object using a resolved path
     */
    fun getCanonicalFile(): File = File(mShell, getCanonicalPath())

    /**
     * The name of this file
     */
    fun getName(): String = mFile.name

    /**
     * Calculates the size of a file or folder
     *
     * Note that on directories with a lot of sub-folders and files,
     * this can be a slow operation
     */
    fun getSize(): Long {
        var size = try {
            if (!mForceShell) mFile.length() else 0L

        } catch (e: Throwable) { 0L }

        if (size == 0L && isType(Type.File)) {
            val path = getAbsolutePath()
            var command: Command? = null
            val cmds = arrayOf<String>(
                    "wc -c < '$path' 2> /dev/null",
                    "wc < '$path' 2> /dev/null"
            )

            for (i in 0 until cmds.size) {
                command = mShell.execute(Command(cmds[i], 0, true))

                if (command.getResultSuccess() && command.trim().getSize() > 0) {
                    size = try {
                        (if (i > 0) command.getLine()!!.split(REGEX_SPACE_SEARCH)[2] else command.getLine()!!).toLong()

                    } catch(e: Throwable) {
                        command = null
                        0L
                    }

                    break
                }

                if (!command?.getResultSuccess()) {
                    size = getDetails().size
                }
            }

        } else if (isType(Type.Directory)) {
            val listing = getListing()
            val path = getAbsolutePath()

            for (entry in listing) {
                size += newFile("$path/$entry").getSize()
            }
        }

        return size
    }

    /**
     * Change ownership (user and group) and permissions on a file or directory.
     *
     * @param user
     *      The uid or -1 if this should not be changed
     *
     * @param group
     *     The gid or -1 if this should not be changed
     *
     * @param mod
     *     The octal permissions or -1 if this should not be changed
     */
    fun changeAccess(user: String?, group: String?, mod: Int): Boolean =
            changeAccess(convertToPermissions(user ?: "unknown"), convertToPermissions(group ?: "unknown"), mod, false)

    /**
     * Change ownership (user and group) and permissions on a file or directory.
     *
     * @param user
     *      The linux user name or NULL if this should not be changed
     *
     * @param group
     *     The linux group name or NULL if this should not be changed
     *
     * @param mod
     *     The octal permissions or -1 if this should not be changed
     *
     * @param recursive
     *     Change the access recursively
     */
    fun changeAccess(user: String?, group: String?, mod: Int, recursive: Boolean): Boolean =
            changeAccess(convertToPermissions(user ?: "unknown"), convertToPermissions(group ?: "unknown"), mod, recursive)

    /**
     * Change ownership (user and group) and permissions on a file or directory.
     *
     * @param user
     *      The uid or -1 if this should not be changed
     *
     * @param group
     *     The gid or -1 if this should not be changed
     *
     * @param mod
     *     The octal permissions or -1 if this should not be changed
     */
    fun changeAccess(user: Int, group: Int, mod: Int): Boolean = changeAccess(user, group, mod, false)

    /**
     * Change ownership (user and group) and permissions on a file or directory.
     *
     * Never use octal numbers for the permissions like '0775'. Always write it as '775', otherwise it will be converted
     * and your permissions will not be changed to the expected value. The reason why this argument is an Integer, is to avoid
     * things like 'a+x', '+x' and such. While this is supported in Linux normally, few Android binaries supports it as they have been
     * stripped down to the bare minimum.
     *
     * @param user
     *      The uid or -1 if this should not be changed
     *
     * @param group
     *     The gid or -1 if this should not be changed
     *
     * @param mod
     *     The octal permissions or -1 if this should not be changed
     *
     * @param recursive
     *     Change the access recursively
     */
    fun changeAccess(user: Int, group: Int, mod: Int, recursive: Boolean): Boolean {
        val command = Command {
            val builder = StringBuilder()
            val bin = if (it == null) "" else it

            if (user >= 0 || group >= 0) {
                builder.append("$bin chown ")

                if (recursive)
                    builder.append("-R ");

                if (user != null && user >= 0)
                    builder.append("" + user);

                if (group != null && group >= 0)
                    builder.append("" + user);
            }

            if (mod > 0) {
                if (builder.length > 0)
                    builder.append(" && ");

                builder.append("$bin chmod ");

                if (recursive)
                    builder.append("-R ");

                builder.append(
                        if (mod <= 777) "0$mod" else mod
                );
            }

            if (builder.length > 0) {
                Call( builder.append(" '" + getAbsolutePath() + "'").toString() )

            } else {
                null
            }
        }

        if (command.getCallSize() > 0) {
            mShell.execute(command)
        }

        return command.getResultSuccess()
    }

    /**
     * This will provide a simple listing of a directory
     */
    fun getListing(): Array<String> {
        if (isType(Type.Any)) {
            val list = try {
                if (!mForceShell) mFile.list() else null

            } catch (e: Throwable) { null }

            if (list != null && list.size > 0) {
                return list
            }

            val path = getAbsolutePath()
            val command = Command()
            val commands = arrayOf<String>(
                    "ls -a1 '$path'",
                    "ls -a '$path'",
                    "ls '$path'"
            )

            for (i in 0 until commands.size) {
                command.reset()
                command.addCall(commands[i], 0, true)

                mShell.execute(command)

                if (command.getResultSuccess()) {
                    if (i == 0) {
                        return command.assort(REGEX_DOT_SEARCH).getArray()

                    } else {
                        /*
                         * Most toolbox versions supports very few flags, and 'ls -a' on toolbox might return
                         * a list, whereas busybox mostly returns columns. So we need to be able to handle both types of output.
                         * Some toolbox versions does not support any flags at all, and they differ from each version about what kind of output
                         * they return.
                         */
                        val lines = command.trim().getString("  ").trim().replace("\t", "  ").split(REGEX_COLUMN_SEARCH)
                        val output = mutableListOf<String>()

                        for (line in lines) {
                            if (!line.equals("") && !line.equals("generated/source/r")) {
                                output.add(line)
                            }
                        }

                        return output.toTypedArray()
                    }
                }
            }
        }

        return arrayOf()
    }

    /**
     * This is the same as [getDetails], only this will provide a whole list
     * with information about each item in a directory.
     *
     * @param maxLines
     *      The max amount of lines to return.
     *      This also excepts negative numbers that defines how many lines to cut off.
     *      0 equals all lines.
     */
    @JvmOverloads
    fun getDetailedListing(maxLines: Int = 0): Array<FileStat> {
        if (isType(Type.Any)) {
            val path = getAbsolutePath()
            val command = Command()
            val commands = arrayOf<String>(
                    "ls -lna '$path'",
                    "ls -la '$path'",
                    "ls -ln '$path'",
                    "ls -l '$path'"
            )

            for (cmd in commands) {
                command.reset()
                command.addCall(cmd, 0, true)

                mShell.execute(command)

                if (command.getResultSuccess()) {
                    val lines = command.trim().getArray()
                    val list = mutableListOf<FileStat>()
                    val maxIndex = if (maxLines == null || maxLines == 0) lines.size else if (maxLines < 0) lines.size + maxLines else maxLines
                    var curIndex = 0

                    for (i in 0 until lines.size) {
                        if (curIndex >= maxIndex) {
                            break
                        }

                        /* There are a lot of different output from the ls command, depending on the arguments supported, whether we used busybox, toybox or toolbox and the versions of the binaries.
                         * We need some serious regexp help to sort through all of the different output options.
                         */
                        val parts = lines[i].replace(REGEX_STAT_SEARCH, "$1|$3|$4|$5|$6|$8|$9").split(REGEX_STAT_SPLIT)

                        if (parts.size == 7) {
                            val statType = when (parts[0].substring(0, 1)) {
                                "-" -> Type.File
                                "d" -> Type.Directory
                                "l" -> Type.Link
                                "b" -> Type.Block
                                "c" -> Type.Character
                                else -> Type.Any
                            }

                            val statAccess = parts[0]
                            val statUser = convertToUID(parts[1])
                            val statGroup = convertToUID(parts[2])
                            val statSize = if (parts[4].equals("null") || !parts[3].equals("null")) 0L else parts[4].toLong()
                            val statMM = if (parts[3].equals("null")) null else "${parts[3]}:${parts[4]}"
                            var statName = if (parts[5].equals("null")) parts[6].substring(parts[6].lastIndexOf("/") + 1) else parts[5].substring(parts[5].lastIndexOf("/") + 1)
                            val statLink = if (parts[5].equals("null")) null else parts[6]
                            val statPermissions = convertToPermissions(statAccess)

                            if (statName.contains('/')) {
                                statName = statName.substring(statName.lastIndexOf('/') + 1)
                            }

                            list.add(FileStat(statName, statLink, statType, statUser, statGroup, statAccess, statPermissions, statMM, statSize))

                            curIndex++
                        }
                    }

                    return list.toTypedArray()
                }
            }
        }

        return arrayOf()
    }

    /**
     * Get information about this file or folder. This will return information like
     * size (on files), path to linked file (on links), permissions, group, user etc...
     */
    fun getDetails(): FileStat {
        var stats = getDetailedListing(1)
        val name = mFile.getName()

        if (stats.size > 0) {
            if (stats[0].name.equals("")) {
                return stats[0].copy(name = name)

            } else if (stats[0].name.equals("name")) {
                return stats[0]

            } else {
                /* On devices without busybox, we could end up using limited toolbox versions
                 * that does not support the "-a" argument in it's "ls" command. In this case,
                 * we need to do a more manual search for folders.
                 */
                stats = getParentFile().getDetailedListing()

                for (stat in stats) {
                    if (stat.name.equals(name)) {
                        return stat
                    }
                }
            }
        }

        return FileStat(name)
    }

    /**
     * Extract the first line of the file
     */
    fun readOneLine(): String? {
        if (isType(Type.Any)) {
            if (!mForceShell) {
                try {
                    val reader = BufferedReader(JavaFileReader(mFile))
                    val line = reader.readLine()
                    reader.close()

                    return line

                } catch (e: Throwable) {
                }
            }

            val command = Command()
            val commands = arrayOf<String>(
                    "sed -n '1p' '" + getAbsolutePath() + "' 2> /dev/null",
                    "cat '" + getAbsolutePath() + "' 2> /dev/null"
            )

            for (cmd in commands) {
                command.reset()
                command.addCall(cmd, 0, true)

                mShell.execute(command)

                if (command.getResultSuccess()) {
                    return command.getLine(0)
                }
            }
        }

        return null
    }

    /**
     * Extract the content from the file and return it.
     *
     * Note: This method will read all the content from a file into the
     * [FileData] object. Use this for small files only. For larger files,
     * consider using [FileReader] for a proper input stream
     */
    fun read(): FileData {
        if (isType(Type.Any)) {
            if (!mForceShell) {
                try {
                    val reader = BufferedReader(JavaFileReader(mFile))
                    val content = mutableListOf<String>()
                    var line: String? = null

                    while ({ line = reader.readLine(); line }() != null) {
                        content.add(line!!)
                    }

                    reader.close()

                    return FileData(content.toTypedArray())

                } catch (e: Throwable) {

                }
            }

            val command = mShell.execute(Command("cat '" + getAbsolutePath() + "' 2> /dev/null", 0, true))

            if (command.getResultSuccess()) {
                return FileData(command.getArray())
            }
        }

        return FileData()
    }

    /**
     * Write a string to the file
     *
     * @param line
     *      The data to write
     *
     * @param append
     *      Whether or not to append the data or overwrite existing
     */
    @JvmOverloads
    fun write(line: String, append: Boolean = false): Boolean = write(arrayOf(line), append)

    /**
     * Write a member of [Data] to the file
     *
     * @param data
     *      The data to write
     *
     * @param append
     *      Whether or not to append the data or overwrite existing
     */
    @JvmOverloads
    fun write(data: Data<*>, append: Boolean = false): Boolean = write(data.getArray(), append)

    /**
     * Write an array of data to the file, each entry is treated as a line
     *
     * Note: Do not use this method for large data. This method is intended for one to a
     * few lines max. Use the [FileWriter] if you need a proper output stream
     *
     * @param lines
     *      The data to write
     *
     * @param append
     *      Whether or not to append the data or overwrite existing
     */
    @JvmOverloads
    fun write(lines: Array<String>, append: Boolean = false): Boolean {
        if (isType(Type.Any)) {
            if (!mForceShell) {
                try {
                    val writer = BufferedWriter(JavaFileWriter(mFile, append))

                    for (line in lines) {
                        writer.write(line)
                        writer.newLine()
                    }

                    writer.close()

                    return true

                } catch (e: Throwable) {
                }
            }

            var redirect = if (append) ">>" else ">"
            val path = getAbsolutePath()
            val command = Command()

            for (line in lines) {
                val escaped = line.replace("'", "\\'")

                command.reset()
                command.addCall("echo '$escaped' $redirect '$path' 2> /dev/null")

                mShell.execute(command)

                if (!command.getResultSuccess()) {
                    return false
                }

                redirect = ">>"
            }

            return true
        }

        return false
    }

    /**
     * Remove the file
     *
     * Folders will be recursively cleaned before deleting
     */
    fun remove(): Boolean {
        if (isType(Type.Any)) {
            val path = getAbsolutePath()

            if (isType(Type.Directory)) {
                val listing = getListing()

                for (entry in listing) {
                    if (!newFile("$path/$entry").remove()) {
                        return false
                    }
                }
            }

            if (!try { if (!mForceShell) mFile.delete() else false } catch(e: Throwable) { false } ) {
                val rmc = if (!isType(Type.Directory) || isType(Type.Link)) "unlink" else "rmdir"
                val command = Command()
                val commands = arrayOf(
                        "rm -rf '$path' 2> /dev/null",
                        "$rmc '$path' 2> /dev/null"
                )

                for (cmd in commands) {
                    command.reset()
                    command.addCall(cmd, 0, true)

                    mShell.execute(command)

                    if (command.getResultSuccess()) {
                        break

                    } else {
                        return false
                    }
                }
            }
        }

        return true
    }

    /**
     * Create a new directory based on the path from this file object
     *
     * @param parents
     *      Whether or not to create any missing parents
     */
    @JvmOverloads
    fun createDirectory(parents: Boolean = false): Boolean {
        if (!isType(Type.Any) && try { ((parents && !if (!mForceShell) mFile.mkdirs() else false) || (!parents && !if (!mForceShell) mFile.mkdir() else false)) } catch (e: Throwable) { false }) {
            val path = getAbsolutePath()

            if (parents && !path.equals("/")) {
                if(!getParentFile().createDirectory(true)) {
                    return false
                }
            }

            val command = mShell.execute(Command("mkdir 'path' 2> /dev/null", 0, true))

            if (command.getResultSuccess()) {
                return true
            }
        }

        return isType(Type.Directory)
    }

    /**
     * Create a link to this file
     *
     * @param linkPath
     *     Path (Including name) to the link which should be created
     */
    fun createLink(linkPath: String): Boolean {
        val linkFile = newFile(linkPath)

        if (isType(Type.Any)) {
            if (!linkFile.isType(Type.Any)) {
                return mShell.execute(Command("ln -s '" + getResolvedPath() + "' '" + linkFile.getAbsolutePath() + "' 2> /dev/null", 0, true)).getResultSuccess()

            } else if (getCanonicalPath().equals(linkFile.getCanonicalPath())) {
                return true
            }
        }

        return false
    }

    /**
     * Move the file to another location
     *
     * @param destPath
     *     The destination path including the file name
     *
     * @param overwrite
     *      Overwrite exiting file, if it exists
     */
    @JvmOverloads
    fun move(destPath: String, overwrite: Boolean = false): Boolean {
        if (isType(Type.Any)) {
            val destFile = newFile(destPath)

            if (!destFile.isType(Type.Any) || (overwrite && destFile.remove())) {
                if (!try { if (!mForceShell) mFile.renameTo(destFile.mFile) else false } catch (e: Throwable) { false }) {
                    if(!mShell.execute(Command("mv '" + getAbsolutePath() + "' '" + destFile.getAbsolutePath() + "'", 0, true)).getResultSuccess()) {
                        if (copy(destPath, true, true) && remove()) {
                            return true
                        }
                    }

                } else {
                    return true
                }
            }
        }

        return false
    }

    /**
     * Copy the file to another location
     *
     * @param destPath
     *     The destination path
     *
     * @param overwrite
     *     Overwrite any existing files. If false, then folders will be merged if a destination folder exist.
     *
     * @param preservePerms
     *     Preserve permissions
     */
    @JvmOverloads
    fun copy(destPath: String, overwrite: Boolean, preservePerms: Boolean = false): Boolean {
        val stat = getDetails()
        var status = false

        if (stat.type != Type.None) {
            val destFile = newFile(destPath)
            val destStat = destFile.getDetails()

            if (stat.type == Type.Link) {
                if (destStat.type == Type.None || (overwrite && destFile.remove())) {
                    if (destStat.link != null && createLink(destStat.link)) {
                        status = true

                    } else {
                        /**
                         * Very few toolbox versions does not provide the link target in it's `ls` command.
                         * Let's try `cp` instead.
                         */
                        status = mShell.execute(Command("cp '" + getAbsolutePath() + "' '" + destFile.getAbsolutePath() + "' 2> /dev/null", 0, true)).getResultSuccess()
                    }
                }

            } else if (stat.type == Type.Directory && (!overwrite || (destStat.type == Type.None || destFile.remove())) && ((destStat.type == Type.None && destFile.createDirectory()) || destStat.type == Type.Directory)) {
                val listing = getListing()
                val absPath = getAbsolutePath()
                val destAbsPath = destFile.getAbsolutePath()

                status = true

                for (entry in listing) {
                    val entryFile = newFile("$absPath/$entry")

                    if (!entryFile.copy("$destAbsPath/$entry", overwrite, preservePerms)) {
                        status = false; break
                    }
                }

            } else if (stat.type != Type.Directory && (destStat.type == Type.None || (overwrite && destFile.remove()))) {
                try {
                    val input = FileInputStream(mFile)
                    val output = FileOutputStream(destFile.mFile)

                    val buffer = ByteArray(1024)
                    var len = 0

                    while ({ len = input.read(buffer); len }() > 0) {
                        output.write(buffer, 0, len)
                    }

                    input.close()
                    output.close()

                    status = true

                } catch (e: Throwable) {
                    status = mShell.execute(Command("cat '" + getAbsolutePath() + "' > '" + destFile.getAbsolutePath() + "' 2> /dev/null", arrayOf(0,130), true)).getResultSuccess()
                }
            }

            if (status && preservePerms) {
                destFile.changeAccess(destStat.user, destStat.group, destStat.permissions)
            }
        }

        return status
    }

    /**
     * Get a new [FileWriter] pointing at this file
     */
    @JvmOverloads
    fun getFileWriter(append: Boolean = false): FileWriter = FileWriter(this, append, mForceShell)

    /**
     * Get a new [FileReader] pointing at this file
     */
    fun getFileReader(): FileReader = FileReader(this, mForceShell)

    /**
     * Extract data from an Android Assets Path (files located in /assets/) and add it to the current file location.
     * If the file already exist, it will be overwritten. Otherwise the file will be created.
     *
     * @param context
     *     An android Context object
     *
     * @param asset
     *     The assets path
     */
    fun extractResource(context: Context, asset: String): Boolean {
        try {
            val input = context.assets.open(asset)
            val status = extractResource(input)
            input.close()

            return status

        } catch (e: Throwable) {
            return false
        }
    }

    /**
     * Extract data from an Android resource id (files located in /res/) and add it to the current file location.
     * If the file already exist, it will be overwritten. Otherwise the file will be created.
     *
     * @param context
     *     An android Context object
     *
     * @param resourceid
     *     The InputStream to read from
     */
    fun extractResource(context: Context, resourceid: Int): Boolean {
        try {
            val input = context.resources.openRawResource(resourceid)
            val status = extractResource(input)
            input.close()

            return status

        } catch (e: Throwable) {
            return false
        }
    }

    /**
     * Extract data from an InputStream and add it to the current file location.
     * If the file already exist, it will be overwritten. Otherwise the file will be created.
     *
     * @param resource
     *     The InputStream to read from
     */
    fun extractResource(resource: InputStream): Boolean {
        if (!isType(Type.Directory) && isType(Type.Any)) {
            try {
                val reader = InputStreamReader(resource)
                val writer = getFileWriter()
                val buffer = CharArray(1024)
                var len = 0

                while ({ len = reader.read(buffer); len }() > 0) {
                    writer.write(buffer, 0, len)
                }

                writer.close()

            } catch (e: Throwable) {
            }
        }

        return false
    }

    /**
     * Make this file executable and run it in the shell
     */
    fun run(): Command {
        val command = Command(getResolvedPath())
        val stat = getDetails()

        if (isType(Type.File) && changeAccess(-1, -1, 777)) {
            mShell.execute(command)
        }

        changeAccess(-1, -1, stat.permissions)

        return command
    }
}