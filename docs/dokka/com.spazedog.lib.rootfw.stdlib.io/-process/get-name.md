[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Process](index.md) / [getName](.)

# getName

`fun getName(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

Get the process name of the current process

If you initialized this object using a process name, this method will return that name.
Otherwise it will locate it in /proc based on the pid.

