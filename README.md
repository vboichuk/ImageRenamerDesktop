## ImageRenamerDesktop

Desktop Java-application for batch renaming images

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
