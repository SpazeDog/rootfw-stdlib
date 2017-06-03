[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [CompCache](.)

# CompCache

`class CompCache : `[`Swap`](../-swap/index.md)

Special extension to [Swap](../-swap/index.md) that handles CompCache/ZRam

This one is able to detect the correct device and load any missing modules.
This should always be used on CompCache/ZRam rather than [Swap](../-swap/index.md) directly.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `CompCache(shell: <ERROR CLASS>)`<br>Special extension to [Swap](../-swap/index.md) that handles CompCache/ZRam |

### Inherited Properties

| Name | Summary |
|---|---|
| [mDevice](../-swap/m-device.md) | `val mDevice: `[`File`](../-file/index.md)<ul><li></li></ul> |
| [mMemory](../-swap/m-memory.md) | `val mMemory: `[`Memory`](../-memory/index.md)<ul><li></li></ul> |
| [mShell](../-swap/m-shell.md) | `val mShell: <ERROR CLASS>`<ul><li></li></ul> |

### Functions

| Name | Summary |
|---|---|
| [setSwapOff](set-swap-off.md) | `fun setSwapOff(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Disable this Swap device. |
| [setSwapOn](set-swap-on.md) | `fun setSwapOn(priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>`fun setSwapOn(priority: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 0, size: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)` = 18): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Enable this Swap device |

### Inherited Functions

| Name | Summary |
|---|---|
| [exists](../-swap/exists.md) | `fun exists(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not this block device exists |
| [getPath](../-swap/get-path.md) | `fun getPath(): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Get the path to the Swap block device |
| [getSwapInfo](../-swap/get-swap-info.md) | `fun getSwapInfo(): `[`SwapInfo`](../-memory/-swap-info/index.md)`?`<br>Get information about this particular swap device, it needs to be active. |
| [isActive](../-swap/is-active.md) | `fun isActive(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not this Swap device is currently active |
