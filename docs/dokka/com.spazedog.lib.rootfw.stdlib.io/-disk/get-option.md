[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Disk](index.md) / [getOption](.)

# getOption

`fun getOption(option: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?`

This can be used to get the value of a specific mount option that was used to attach the file system.

Note that options like noexec, nosuid and nodev does not have any values and will return themselves as value.
This method is used to get values from options like gid=xxxx, mode=xxxx and size=xxxx where xxxx is the value.

### Parameters

`option` -

```

```
    The name of the option to find
```

```

**Return**

```

```
    The option value or `NULL` if device is not mounted
```

```

