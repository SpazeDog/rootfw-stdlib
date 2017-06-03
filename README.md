RootFW StdLib
=============

Extension library for RootFW

> This library depends on [RootFW](https://github.com/SpazeDog/RootFW) >= 5.x

**Checkout the [Documentation Page](docs/dokka/index.md) for further info**

### Include Library
-----------

**Maven**

Reflect Tools is available in Maven respository at [Bintray](https://bintray.com/dk-zero-cool/maven/rootfw-stdlib/view) and can be accessed via jCenter.

```
dependencies {
    compile 'com.spazedog.lib.rootfw:rootfw-stdlib:$version'
}
```

**Android Studio**

First download the [rootfw-stdlib-release.aar](https://github.com/SpazeDog/rootfw-stdlib/raw/master/releases/rootfw-stdlib-release.aar) file.

Place the file in something like the `libs` folder in your module.

Open your `build.gradle` file for your module _(not the main project version)_.

```
dependencies {
    compile(name:'rootfw-stdlib-release', ext:'aar')
}

repositories {
    flatDir {
        dirs 'libs'
    }
}
```

