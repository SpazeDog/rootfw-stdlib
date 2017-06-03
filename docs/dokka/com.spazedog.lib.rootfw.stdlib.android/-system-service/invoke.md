[com.spazedog.lib.rootfw.stdlib.android](../index.md) / [SystemService](index.md) / [invoke](.)

# invoke

`fun invoke(method: `[`String`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)`, reply: <ERROR CLASS>?, vararg args: `[`Any`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Invoke an IPC method on the system service that this instance is connected to

Note that the return state [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html) is only an indication on whether or not the call
made it to the service. Any errors from the service itself is service specific and can be
checked via the reply parcel.

### Parameters

`method` -

```

```
    The method to call
```

```

`reply` -

```

```
    A reply parcel that will be populated with the return data from the method.
```


```
    Can be `NULL` if no reply is wanted.
```

```

`args` -

```

```
    Arguments to be parsed to the method. A parcel will be created for the arguments.
```


```
    You can also parse your own populated parcel that will be used instead.
```

```

