[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [write](.)

# write

`fun write(line: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Write a string to the file

### Parameters

`line` -

```

```
    The data to write
```

```

`append` -

```

```
    Whether or not to append the data or overwrite existing
```

```

`fun write(data: <ERROR CLASS><out <ERROR CLASS>>, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Write a member of [Data](#) to the file

### Parameters

`data` -

```

```
    The data to write
```

```

`append` -

```

```
    Whether or not to append the data or overwrite existing
```

```

`fun write(lines: `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`>, append: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)` = false): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Write an array of data to the file, each entry is treated as a line

Note: Do not use this method for large data. This method is intended for one to a
few lines max. Use the [FileWriter](../-file-writer/index.md) if you need a proper output stream

### Parameters

`lines` -

```

```
    The data to write
```

```

`append` -

```

```
    Whether or not to append the data or overwrite existing
```

```

