[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [extractResource](.)

# extractResource

`fun extractResource(context: <ERROR CLASS>, asset: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Extract data from an Android Assets Path (files located in /assets/) and add it to the current file location.
If the file already exist, it will be overwritten. Otherwise the file will be created.

### Parameters

`context` -

```

```
    An android Context object
```

```

`asset` -

```

```
    The assets path
```

```

`fun extractResource(context: <ERROR CLASS>, resourceid: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Extract data from an Android resource id (files located in /res/) and add it to the current file location.
If the file already exist, it will be overwritten. Otherwise the file will be created.

### Parameters

`context` -

```

```
    An android Context object
```

```

`resourceid` -

```

```
    The InputStream to read from
```

```

`fun extractResource(resource: `[`InputStream`](http://docs.oracle.com/javase/6/docs/api/java/io/InputStream.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Extract data from an InputStream and add it to the current file location.
If the file already exist, it will be overwritten. Otherwise the file will be created.

### Parameters

`resource` -

```

```
    The InputStream to read from
```

```

