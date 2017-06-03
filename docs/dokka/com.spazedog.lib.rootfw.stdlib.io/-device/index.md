[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Device](.)

# Device

`open class Device`

Collect a list of processes running on the device or control the device power,
such as power off, reboot etc.

### Types

| Name | Summary |
|---|---|
| [ProcessInfo](-process-info/index.md) | `data class ProcessInfo`<br>This is a container class used to store information about a process. |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Device(shell: <ERROR CLASS>)`<br>Collect a list of processes running on the device or control the device power, such as power off, reboot etc. |

### Properties

| Name | Summary |
|---|---|
| [mShell](m-shell.md) | `val mShell: <ERROR CLASS>`<ul><li></li></ul> |

### Functions

| Name | Summary |
|---|---|
| [getProcessList](get-process-list.md) | `fun getProcessList(pname: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`ProcessInfo`](-process-info/index.md)`>`<br>Return a list of all active processes |
| [reboot](reboot.md) | `fun reboot(context: <ERROR CLASS>? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Reboots the device |
| [rebootRecovery](reboot-recovery.md) | `fun rebootRecovery(context: <ERROR CLASS>? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Reboots the device into the recovery |
| [rebootSoft](reboot-soft.md) | `fun rebootSoft(context: <ERROR CLASS>? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Makes a soft reboot of the device |
| [shutdown](shutdown.md) | `fun shutdown(context: <ERROR CLASS>? = null): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Powers down the device |
