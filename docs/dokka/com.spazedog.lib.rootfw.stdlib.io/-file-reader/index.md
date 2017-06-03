[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [FileReader](.)

# FileReader

`class FileReader : `[`Reader`](http://docs.oracle.com/javase/6/docs/api/java/io/Reader.html)

This class is used to read from a file. Unlike [java.io.FileReader](http://docs.oracle.com/javase/6/docs/api/java/io/FileReader.html), this class
will fallback on a SuperUser stream whenever a read action is not allowed by the application.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `FileReader(file: `[`File`](http://docs.oracle.com/javase/6/docs/api/java/io/File.html)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`<br>`FileReader(file: `[`File`](../-file/index.md)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)``FileReader(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, forcedShell: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false)`<br>This class is used to read from a file. Unlike [java.io.FileReader](http://docs.oracle.com/javase/6/docs/api/java/io/FileReader.html), this class will fallback on a SuperUser stream whenever a read action is not allowed by the application. |

### Functions

| Name | Summary |
|---|---|
| [close](close.md) | `fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [mark](mark.md) | `fun mark(readLimit: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [markSupported](mark-supported.md) | `fun markSupported(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [read](read.md) | `fun read(buffer: `[`CharArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-char-array/index.html)`, offset: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, count: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html) |
| [ready](ready.md) | `fun ready(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) |
| [reset](reset.md) | `fun reset(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html) |
| [skip](skip.md) | `fun skip(charCount: `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html)`): `[`Long`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-long/index.html) |
