[com.spazedog.lib.rootfw.stdlib.io](.)

## Package com.spazedog.lib.rootfw.stdlib.io

### Types

| Name | Summary |
|---|---|
| [CompCache](-comp-cache/index.md) | `class CompCache : `[`Swap`](-swap/index.md)<br>Special extension to [Swap](-swap/index.md) that handles CompCache/ZRam |
| [Device](-device/index.md) | `open class Device`<br>Collect a list of processes running on the device or control the device power, such as power off, reboot etc. |
| [Disk](-disk/index.md) | `class Disk`<br>Used to handle device files |
| [File](-file/index.md) | `class File`<br>Extended version of `java.io.File` (Not a member) with additional tools and ability to use `root` as fallback to force access to any file/directory |
| [FileReader](-file-reader/index.md) | `class FileReader : `[`Reader`](http://docs.oracle.com/javase/6/docs/api/java/io/Reader.html)<br>This class is used to read from a file. Unlike [java.io.FileReader](http://docs.oracle.com/javase/6/docs/api/java/io/FileReader.html), this class will fallback on a SuperUser stream whenever a read action is not allowed by the application. |
| [FileWriter](-file-writer/index.md) | `class FileWriter : `[`Writer`](http://docs.oracle.com/javase/6/docs/api/java/io/Writer.html)<br>This class is used to write to a file. Unlike [java.io.FileWriter](http://docs.oracle.com/javase/6/docs/api/java/io/FileWriter.html), this class will fallback on a SuperUser stream whenever a write action is not allowed by the application. |
| [Filesystem](-filesystem/index.md) | `class Filesystem` |
| [Memory](-memory/index.md) | `class Memory`<br>This class is used to get information about the device memory. |
| [Process](-process/index.md) | `class Process`<br>Handle a single specific process running on the device |
| [Swap](-swap/index.md) | `open class Swap`<br>Handle a specific swap device |
