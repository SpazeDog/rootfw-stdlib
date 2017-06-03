[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [setForcedShell](.)

# setForcedShell

`fun setForcedShell(flag: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Unit`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-unit/index.html)

Force the shell to be used on all operations

By default, this class tries to use the Java API's for all file operations.
It only uses shell access/root whenever we don't have access to a particular file.
But some files must be handled by the shell in newer Android releases, one example being
reading from `/proc/mounts`, because even though applications has access to this file,
they don't have access to the original version. A root process however does.

### Parameters

`flag` -

```

```
    Whether or not to force operations through the shell
```

```

