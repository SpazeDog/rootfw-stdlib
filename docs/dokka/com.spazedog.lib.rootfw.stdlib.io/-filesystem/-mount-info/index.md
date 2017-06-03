[com.spazedog.lib.rootfw.stdlib.io](../../index.md) / [Filesystem](../index.md) / [MountInfo](.)

# MountInfo

`data class MountInfo`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `MountInfo(device: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = "", location: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = "", fstype: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)` = "", options: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`> = arrayOf<String>())` |

### Properties

| Name | Summary |
|---|---|
| [device](device.md) | `val device: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [disk](disk.md) | `val disk: `[`Disk`](../../-disk/index.md)`?`<br>Returns a [Disk](../../-disk/index.md) instance pointing to this Disk or 'NULL' if not possible |
| [fstype](fstype.md) | `val fstype: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [location](location.md) | `val location: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html) |
| [options](options.md) | `val options: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>` |
