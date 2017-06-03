[com.spazedog.lib.rootfw.stdlib.io](../index.md) / [File](index.md) / [read](.)

# read

`fun read(): `[`FileData`](-file-data/index.md)

Extract the content from the file and return it.

Note: This method will read all the content from a file into the
[FileData](-file-data/index.md) object. Use this for small files only. For larger files,
consider using [FileReader](../-file-reader/index.md) for a proper input stream

