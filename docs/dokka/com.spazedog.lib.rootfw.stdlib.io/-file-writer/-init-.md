[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [FileWriter](index.md) / [&lt;init&gt;](.)

# &lt;init&gt;

`FileWriter(file: `[`File`](http://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`
`FileWriter(file: `[`File`](../-file/index.md)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)``FileWriter(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`

This class is used to write to a file. Unlike [java.io.FileWriter](http://docs.oracle.com/javase/6/docs/api/java/io/FileWriter.html), this class
will fallback on a SuperUser stream whenever a write action is not allowed by the application.

