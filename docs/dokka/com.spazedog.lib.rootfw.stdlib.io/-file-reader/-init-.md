[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [FileReader](index.md) / [&lt;init&gt;](.)

# &lt;init&gt;

`FileReader(file: `[`File`](http://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`
`FileReader(file: `[`File`](../-file/index.md)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)``FileReader(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`

This class is used to read from a file. Unlike [java.io.FileReader](http://docs.oracle.com/javase/6/docs/api/java/io/FileReader.html), this class
will fallback on a SuperUser stream whenever a read action is not allowed by the application.

