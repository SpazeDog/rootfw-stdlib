[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Swap](.)

# Swap

`open class Swap`

Handle a specific swap device

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Swap(shell: <ERROR CLASS>, device: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>Handle a specific swap device |

### Properties

| Name | Summary |
|---|---|
| [mDevice](m-device.md) | `val mDevice: `[`File`](../-file/index.md)<ul><li></li></ul> |
| [mMemory](m-memory.md) | `val mMemory: `[`Memory`](../-memory/index.md)<ul><li></li></ul> |
| [mShell](m-shell.md) | `val mShell: <ERROR CLASS>`<ul><li></li></ul> |

### Functions

| Name | Summary |
|---|---|
| [exists](exists.md) | `fun exists(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not this block device exists |
| [getPath](get-path.md) | `fun getPath(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Get the path to the Swap block device |
| [getSwapInfo](get-swap-info.md) | `fun getSwapInfo(): `[`SwapInfo`](../-memory/-swap-info/index.md)`?`<br>Get information about this particular swap device, it needs to be active. |
| [isActive](is-active.md) | `fun isActive(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not this Swap device is currently active |
| [setSwapOff](set-swap-off.md) | `open fun setSwapOff(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Disable this Swap device. |
| [setSwapOn](set-swap-on.md) | `open fun setSwapOn(priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Enable this Swap device |

### Inheritors

| Name | Summary |
|---|---|
| [CompCache](../-comp-cache/index.md) | `class CompCache : Swap`<br>Special extension to Swap that handles CompCache/ZRam |
