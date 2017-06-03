[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [Device](index.md) / [getProcessList](.)

# getProcessList

`fun getProcessList(pname: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`? = null): `[`Array`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-array/index.html)`<`[`ProcessInfo`](-process-info/index.md)`>`

Return a list of all active processes

### Parameters

`pname` -

```

```
    Name of a process or `NULL` to include all. If name is provided,
```


```
    it will only include all active processes with that name.
```


```
    This can reduce a lot of overheat if you are only looking for a
```


```
    specific process. It's still going to have to scan all process files in /proc,
```


```
    but the provided name will limit the processing of the data in those files.
```

```

