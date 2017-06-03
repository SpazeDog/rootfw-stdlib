[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Disk](index.md) / [mount](.)

# mount

`fun mount(location: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, fstype: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null, vararg options: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Mount this device or folder to a specified location.

If this device is a folder, it will create a `bind` mount to the location.

### Parameters

`location` -

```

```
    Mount location
```

```

`fstype` -

```

```
    Optional file system type, or `NULL` for `auto`
```

```

`options` -

```

```
    One or more mount options
```

```

