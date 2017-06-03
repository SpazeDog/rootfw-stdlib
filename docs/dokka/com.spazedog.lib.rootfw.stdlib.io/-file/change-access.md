[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [changeAccess](.)

# changeAccess

`fun changeAccess(user: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, group: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, mod: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)
`fun changeAccess(user: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, group: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, mod: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Change ownership (user and group) and permissions on a file or directory.

### Parameters

`user` -

```

```
    The uid or -1 if this should not be changed
```

```

`group` -

```

```
    The gid or -1 if this should not be changed
```

```

`mod` -

```

```
    The octal permissions or -1 if this should not be changed
```

```

`fun changeAccess(user: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, group: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`?, mod: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, recursive: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Change ownership (user and group) and permissions on a file or directory.

### Parameters

`user` -

```

```
    The linux user name or NULL if this should not be changed
```

```

`group` -

```

```
    The linux group name or NULL if this should not be changed
```

```

`mod` -

```

```
    The octal permissions or -1 if this should not be changed
```

```

`recursive` -

```

```
    Change the access recursively
```

```

`fun changeAccess(user: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, group: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, mod: `[`Int`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-int/index.html)`, recursive: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Change ownership (user and group) and permissions on a file or directory.

Never use octal numbers for the permissions like '0775'. Always write it as '775', otherwise it will be converted
and your permissions will not be changed to the expected value. The reason why this argument is an Integer, is to avoid
things like 'a+x', '+x' and such. While this is supported in Linux normally, few Android binaries supports it as they have been
stripped down to the bare minimum.

### Parameters

`user` -

```

```
    The uid or -1 if this should not be changed
```

```

`group` -

```

```
    The gid or -1 if this should not be changed
```

```

`mod` -

```

```
    The octal permissions or -1 if this should not be changed
```

```

`recursive` -

```

```
    Change the access recursively
```

```

