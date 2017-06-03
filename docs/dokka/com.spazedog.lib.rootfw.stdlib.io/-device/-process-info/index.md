[com.spazedog.lib.rootfw.stdlib.io](../../index.md) / [Device](../index.md) / [ProcessInfo](.)

# ProcessInfo

`data class ProcessInfo`

This is a container class used to store information about a process.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `ProcessInfo(path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = "", pid: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = -1)`<br>This is a container class used to store information about a process. |

### Properties

| Name | Summary |
|---|---|
| [name](name.md) | `val name: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>The name of the process |
| [path](path.md) | `val path: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>The process path (Could be NULL) as not all processes has a path assigned |
| [pid](pid.md) | `val pid: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>The pid of the process |
| [process](process.md) | `val process: `[`Process`](../../-process/index.md)`?`<br>Returns a [Process](../../-process/index.md) instance pointing to this process or 'NULL' if not possible |
