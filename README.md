## ImageRenamerDesktop

Desktop Java application for batch renaming images using user-defined patterns.


### Placeholder

The application supports the following placeholders in patterns:

| Placeholder | Description | Example |
|------------|----------|---------|
| `{original[:format]}` | Original file name | `IMG_1234` |
| `{date[:format][\|default]}` | Date/time from EXIF | `2024.01.15_14-30` |
| `{camera[:format][\|default]}` | Camera model from EXIF | `Canon EOS 5D` |
| `{hash[:format]}` | MD5 checksum of the file | `e3b0c44298fc1c149afbf4c8996fb924` |
| `{ext[:format]}` | File extension | `JPG` |


### Formats

**Supported formats for placeholders (except `date`):**
- `:N` - first N characters (for example, `:10`)
- `:lower` - lower case
- `:upper` - upper case
- `:lower:5` - format combination (first 5 characters in lower case)

**Supported formats for the `date` placeholder:**
- `yyyy` - year (e.g., 2026)
- `M` - month (1-12)
- `MM` - month (01-12)
- `MMM` - month abbreviation (Jan-Dec)
- `MMMM` - full month name (January-December)
- `d` - day of month (1-31)
- `dd` - day of month (01-31)
- `hh` - hours (1-12)
- `HH` - hours (0-23)
- `mm` - minute (00-59)
- `ss` - second (00-59)


### Default values

For `date` and `camera`, you can specify a default value using `|`:

`{date|nodate}` - if the date is not available (e.g., the file contains no EXIF data), "_nodate_" will be substituted

`{camera|unknown}` - if the camera model is not available (e.g., the file contains no EXIF data), "_unknown_" will be substituted


### Workflow Process

1. Scanning - searches for all JPG files in the specified directory

2. Metadata Extraction - for each file, the following data is read:
  - EXIF data (shooting date, camera model)
  - MD5 hash
  - Original name and extension

4. Name Generation - applies the template to the metadata

5. Preview - displays the list of changes

6. Confirmation - requests confirmation before applying changes

7. Renaming - safely renames the files


## Technologies

- **Java 8+** 
- **SLF4J**
- **JUnit**
- **Maven**

### Building the Project

**Maven:**
```bash
git clone https://github.com/vboichuk/ImageRenamerDesktop.git
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
