[com.spazedog.lib.rootfw.stdlib.android](../index.md) / [SystemService](index.md) / [daemon](.)

# daemon

`fun daemon(): <ERROR CLASS>`

Check whether this instance is running in daemon or request mode

`fun daemon(flag: `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)`): `[`Boolean`](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)

Switch between daemon state and request state

In daemon state, the [ShellStreamer](#) is permanently connected to
an Android service, providing dirrect and fast access to it. This
is useful if you make constant calls to a service over a long period of time.

In request mode, each call to the service will connect to the service,
handle the request and disconnect again. This is best if you only make
a call ones in awhile.

Note that daemon mode will block the [ShellStream](#) from any further use
as long as it's connected to the Android service. If you are using the
[ShellStream](#) for other things, like a [Shell](#), then use either request mode
or use a different [ShellStream](#)

Also note that each switch between daemon/request mode could kill the [ShellStream](#)
if any errors occures during the switch. This will close any [Shell](#) instances
connected through the [ShellStream](#).

### Parameters

`flag` -

```

```
    `True` to switch to daemon mode, `False` to switch to request mode
```

```

