[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Disk](.)

# Disk

`class Disk`

Used to handle device files

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `Disk(shell: <ERROR CLASS>, disk: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`<br>Used to handle device files |

### Functions

| Name | Summary |
|---|---|
| [getDiskInfo](get-disk-info.md) | `fun getDiskInfo(): `[`DiskInfo`](../-filesystem/-disk-info/index.md)`?`<br>Extended version of [getMountInfo](get-mount-info.md) |
| [getMountInfo](get-mount-info.md) | `fun getMountInfo(): `[`MountInfo`](../-filesystem/-mount-info/index.md)`?`<br>Get mount information about this disk |
| [getOption](get-option.md) | `fun getOption(option: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`<br>This can be used to get the value of a specific mount option that was used to attach the file system. |
| [hasOption](has-option.md) | `fun hasOption(option: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Check if this device was mounted with a specific mount option |
| [isMounted](is-mounted.md) | `fun isMounted(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This is used to check whether the current device or folder is attached to a location (Mounted). |
| [mount](mount.md) | `fun mount(location: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, fstype: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, vararg options: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Mount this device or folder to a specified location. |
| [move](move.md) | `fun move(dest: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method is used to move a mount location to another location. |
| [remount](remount.md) | `fun remount(vararg options: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>Remount a currently mounted device with altered options. |
| [unmount](unmount.md) | `fun unmount(): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)<br>This method is used to remove an attachment of a device or folder (unmount). |
