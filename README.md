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

Default values can be empty

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

## Building the Project

**Requirements**

- JDK 8 +
- Maven 3.6+

**Building:**

```bash
git clone https://github.com/vboichuk/ImageRenamerDesktop.git
cd ImageRenamerDesktop
mvn clean package
```

## Usage

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

**Sample usage** 
```bash
java -jar target/FileRenamerDesktop-1.0-SNAPSHOT-jar-with-dependencies.jar \
-d ~/Photo \
-t "{camera|UndefinedCamera}/IMG_{date:yyyy.MM.dd_HHmm-|}{hash:6}.{ext:upper}" \
rename
```

Application output

```bash
Image processing result:
Processed: 20
Skipped: 0
Failed: 0

Files to rename (20):
IMG_014.jpg → Google/IMG_2025.01.01_1639-708f9b.JPG
IMG_001.jpg → Apple/IMG_2024.03.16_1118-0cc347.JPG
IMG_003.jpg → Google/IMG_2025.03.29_1746-6a7b0b.JPG
IMG_006.jpg → Google/IMG_2025.01.11_1855-31cd0a.JPG
IMG_007.jpg → Google/IMG_2024.09.21_1806-32bf26.JPG
IMG_005.jpg → Google/IMG_2025.02.04_1543-0011f4.JPG
IMG_010.jpg → Apple/IMG_2024.03.16_1119-55d6e1.JPG
IMG_004.jpg → Google/IMG_2024.09.21_1806-6d45e8.JPG
IMG_021.jpg → Google/IMG_2025.03.10_1624-a89ab3.JPG
IMG_020.jpg → Canon/IMG_2014.11.01_1458-a6b76d.JPG
IMG_008.jpg → Apple/IMG_2024.03.16_1120-32e029.JPG
IMG_022.jpg → Google/IMG_2025.01.01_1639-b19179.JPG
IMG_027.jpg → Google/IMG_2024.09.21_1805-f00e2d.JPG
IMG_026.jpg → UndefinedCamera/IMG_e11104.JPG
IMG_024.jpg → UndefinedCamera/IMG_c6f050.JPG
IMG_030.jpg → Canon/IMG_2025.03.26_1336-fd1746.JPG
IMG_018.jpg → Google/IMG_2024.09.21_1805-94090b.JPG
IMG_019.jpg → Apple/IMG_2024.03.16_1124-a2b1d3.JPG
IMG_031.jpg → Google/IMG_2025.02.04_0850-ffa1e3.JPG
IMG_025.jpg → Apple/IMG_2024.03.16_1124-d8995d.JPG
Confirm rename? (y/Y/+):
```

Files after rename:

```
├── Apple
│   ├── IMG_2024.03.16_1118-0cc347.JPG
│   ├── IMG_2024.03.16_1119-55d6e1.JPG
│   ├── IMG_2024.03.16_1120-32e029.JPG
│   ├── IMG_2024.03.16_1124-a2b1d3.JPG
│   └── IMG_2024.03.16_1124-d8995d.JPG
├── Canon
│   ├── IMG_2014.11.01_1458-a6b76d.JPG
│   └── IMG_2025.03.26_1336-fd1746.JPG
├── Google
│   ├── IMG_2024.09.21_1805-94090b.JPG
│   ├── IMG_2024.09.21_1805-f00e2d.JPG
│   ├── IMG_2024.09.21_1806-32bf26.JPG
│   ├── IMG_2024.09.21_1806-6d45e8.JPG
│   ├── IMG_2025.01.01_1639-708f9b.JPG
│   ├── IMG_2025.01.01_1639-b19179.JPG
│   ├── IMG_2025.01.11_1855-31cd0a.JPG
│   ├── IMG_2025.02.04_0850-ffa1e3.JPG
│   ├── IMG_2025.02.04_1543-0011f4.JPG
│   ├── IMG_2025.03.10_1624-a89ab3.JPG
│   └── IMG_2025.03.29_1746-6a7b0b.JPG
└── UndefinedCamera
    ├── IMG_c6f050.JPG
    └── IMG_e11104.JPG
```

_Files `IMG_c6f050.JPG`, `IMG_e11104.JPG` have no EXIF data_
