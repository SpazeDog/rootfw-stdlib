[com.spazedog.lib.rootfw.stdlib.android](../index.md) / [SystemService](index.md) / [&lt;init&gt;](.)

# &lt;init&gt;

`SystemService(context: <ERROR CLASS>, service: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, iface: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

Create a new [Shell](#) instance using a new default [ShellStream](#)

### Parameters

`context` -

```

```
    An Android [Context]
```

```

`service` -

```

```
    The service to connect to, f.eks. [Context.POWER_SERVICE]
```

```

`iface` -

```

```
    Full name of the AIDL class that is used with the service
```

```

`SystemService(context: <ERROR CLASS>, stream: <ERROR CLASS>, service: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, iface: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`)`

```

```
    Throw [RuntimeException] on error, like when an interface file could not be found.
```

```

### Parameters

`context` -

```

```
    An Android [Context]
```

```

`stream` -

```

```
    A [ShellStream] to run Anycall from
```

```

`service` -

```

```
    The service to connect to, f.eks. [Context.POWER_SERVICE]
```

```

`iface` -

```

```
    Full name of the AIDL class that is used with the service
```

```

**Constructor**

```

```
    Throw [RuntimeException] on error, like when an interface file could not be found.
```

```

