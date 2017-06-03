[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [getResolvedPath](.)

# getResolvedPath

`fun getResolvedPath(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)

Get a real absolute path

Java's getAbsolutePath is not a fully resolved path. Something like ./file could be returned as /folder/folder2/.././file or simular.
This method however will resolve a path and return a fully absolute path /folder/file. It is a bit slower, so only use it when this is a must.

