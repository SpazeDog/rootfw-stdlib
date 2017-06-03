[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Memory](.)

# Memory

`class Memory`

This class is used to get information about the device memory.

It can provide information about total memory and swap space, free memory and swap etc.
It can also be used to check the device support for CompCache/ZRam and Swap along with
providing a list of active Swap and CompCache/ZRam devices.

### Types

| Name | Summary |
|---|---|
| [MemoryInfo](-memory-info/index.md) | `data class MemoryInfo` |
| [SwapInfo](-swap-info/index.md) | `data class SwapInfo` |

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Memory(shell: <ERROR CLASS>)`<br>This class is used to get information about the device memory. |

### Properties

| Name | Summary |
|---|---|
| [mShell](m-shell.md) | `val mShell: <ERROR CLASS>`<ul><li></li></ul> |

### Functions

| Name | Summary |
|---|---|
| [getMemInfo](get-mem-info.md) | `fun getMemInfo(): `[`MemoryInfo`](-memory-info/index.md)<br>Get memory information like ram usage, ram total, cached memory, swap total etc. |
| [getSwapInfo](get-swap-info.md) | `fun getSwapInfo(): `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`SwapInfo`](-swap-info/index.md)`>`<br>Get a list of all active SWAP devices. |
| [getSwappiness](get-swappiness.md) | `fun getSwappiness(): `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)<br>Get the current swappiness level. |
| [hasCompCacheSupport](has-comp-cache-support.md) | `fun hasCompCacheSupport(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not CompCache/ZRam is supported by the kernel. |
| [hasSwapSupport](has-swap-support.md) | `fun hasSwapSupport(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check whether or not Swap is supported by the kernel. |
| [setSwappiness](set-swappiness.md) | `fun setSwappiness(level: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Change the swappiness level. |
