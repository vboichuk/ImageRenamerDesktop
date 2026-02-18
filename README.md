## ImageRenamerDesktop

Desktop Java-application for batch renaming images by user pattern.

The application supports next placeholders in pattern:

| Placeholder | Description | Example |
|------------|----------|---------|
| `{original[:format]}` | Original file name | `IMG_1234` |
| `{date[:pattern][\|default]}` | Date/time from EXIF | `2024.01.15_14-30` |
| `{camera[:format][\|default]}` | Camera model | `Canon EOS 5D` |
| `{hash[:format]}` | MD5 checksum of file | `e3b0c44298fc1c149afbf4c8996fb924` |
| `{ext[:format]}` | File extension | `JPG` |

For placeholders you can apply formats:
- `:N` - first N characters (for example, `:10`)
- `:lower` - lower case
- `:upper` - upper case
- `:lower:5` - format combination (first 5 characters in lower case)

### default values

For date and camera, you can specify a default value using |:

`{date|nodate}` - if the date is not available (e.g., the file contains no EXIF data), "nodate" will be substituted
`{camera|unknown}` - if the camera model is not available (e.g., the file contains no EXIF data), "unknown" will be substituted

For example:
Pattern `{date:yyyy.MM.dd}-{hash:6}.{ext:upper}` will make filename 
`2024.01.15-e3b0c4.JPG`

## Technologies

- **Java 8+** 
- **SLF4J**
- **JUnit**
- **Maven**

### Build project

**Maven:**
```bash
git clone https://https://github.com/vboichuk/ImageRenamerDesktop.git
cd ImageRenamerDesktop
mvn clean package
```

### Usage

```bash
java -jar target/FileRenamerDesktop-1.0-SNAPSHOT-jar-with-dependencies.jar [-hV] [-d=<directory>] [-t=<template>] [COMMAND]
```


```bash
Help:
  -d, --directory=<directory>
                  Directory. Current directory by default
  -h, --help      Show this help message and exit.
  -t, --template=<template>
                  Naming pattern for renaming (e.g. "{date:yyyy-MM-dd}_{camera}.{ext}")
  -V, --version   Print version information and exit.
Commands:
  rename    Rename files in the specified directory
```
