[com.spazedog.lib.rootfw.stdlib.android](../index.md) / [SystemService](.)

# SystemService

`class SystemService`

Connect to a system service and get access to all it's IPC method

Android has a great deal of hidden methods in their IPC interfaces,
and also a lot of methods with restricted access that normal apps cannot
get permission for. This class can access any method defined in any
IPC interface for any system service on Android, as root. It uses
a small native binary from the Anycall project to call the services
using a root shell via [ShellStream](#).

Get more access without the need to turn to Xposed or similar.
This only requires a device with root access.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `SystemService(context: <ERROR CLASS>, service: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, iface: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>Create a new [Shell](#) instance using a new default [ShellStream](#)`SystemService(context: <ERROR CLASS>, stream: <ERROR CLASS>, service: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, iface: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

```

```
    Throw [RuntimeException] on error, like when an interface file could not be found.<br>```
<br>```
<br> |

### Functions

| Name | Summary |
|---|---|
| [active](active.md) | `fun active(): <ERROR CLASS>`<br>Check if this instance if active, that is, whether we have a shell connection or not |
| [close](close.md) | `fun close(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Close the daemon, if in daemon mode and cleanup this instance |
| [daemon](daemon.md) | `fun daemon(): <ERROR CLASS>`<br>Check whether this instance is running in daemon or request mode`fun daemon(flag: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Switch between daemon state and request state |
| [destroy](destroy.md) | `fun destroy(): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)<br>Destroy everything, incl. the [ShellStream](#) |
| [getStream](get-stream.md) | `fun getStream(): <ERROR CLASS>`<br>Get the [ShellStream](#) that is being used by this instance |
| [invoke](invoke.md) | `fun invoke(method: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, reply: <ERROR CLASS>?, vararg args: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Invoke an IPC method on the system service that this instance is connected to |
| [service](service.md) | `fun service(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Name of the Android service that we are connected to |

### Companion Object Properties

| Name | Summary |
|---|---|
| [ABIS](-a-b-i-s.md) | `val ABIS: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>`<br>A list of all supported ABI's by this device. The list is ordered by the most preferred one first. |
