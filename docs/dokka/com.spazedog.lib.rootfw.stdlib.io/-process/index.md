[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Process](.)

# Process

`class Process`

Handle a single specific process running on the device

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Process(shell: <ERROR CLASS>, pid: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`)`<br>`Process(shell: <ERROR CLASS>, pname: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)` |

### Functions

| Name | Summary |
|---|---|
| [getName](get-name.md) | `fun getName(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>Get the process name of the current process |
| [getPid](get-pid.md) | `fun getPid(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Get the pid of the current process. |
| [getPids](get-pids.md) | `fun getPids(): `[`IntArray`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int-array/index.html)<br>Get a list of all pid's for this process name. |
| [kill](kill.md) | `fun kill(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Kill this process |
