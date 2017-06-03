[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [FileWriter](.)

# FileWriter

`class FileWriter : `[`Writer`](http://docs.oracle.com/javase/6/docs/api/java/io/Writer.html)

This class is used to write to a file. Unlike [java.io.FileWriter](http://docs.oracle.com/javase/6/docs/api/java/io/FileWriter.html), this class
will fallback on a SuperUser stream whenever a write action is not allowed by the application.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `FileWriter(file: `[`File`](http://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`<br>`FileWriter(file: `[`File`](../-file/index.md)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)``FileWriter(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`<br>This class is used to write to a file. Unlike [java.io.FileWriter](http://docs.oracle.com/javase/6/docs/api/java/io/FileWriter.html), this class will fallback on a SuperUser stream whenever a write action is not allowed by the application. |

### Functions

| Name | Summary |
|---|---|
| [close](close.md) | `fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [flush](flush.md) | `fun flush(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [write](write.md) | `fun write(buf: `[`CharArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html)`?, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, len: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
