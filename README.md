# Paper Downloader

A tiny and simple utility to download PaperMC server builds.

## Usage

```bash
java -jar papermc-downloader.jar
```

Supported options:
- -p / --mc-version: Paper server version to download (latest by default).
- -b / --mc-build: Paper server build number to download (latest by default).
- -o / --out: Path where the paper JAR file will be downloaded.
- -d / --dry-run: If set, no real download will be performed.
- -w / --overwrite: If set a previously downloaded JAR file will be overwritten.
- -h / --help: Display the command help message.

If no version or build is specified using the command option, you still can use environment variables to define them:
- MC_VERSION: Paper server version to download (latest by default).
- MC_BUILD: Paper server build number to download (latest by default).

## Building

This project is built on top of the Java programming language and Maven, you need both a JDK, version 25 or higher, and Maven to build this project.
To build the application, run this command in the project root directory:

```bash
mvn package
```

Now you can find the compiled jar file in the `target` directory (named `papermc-downloader.jar`).

## License

This work is licensed under a
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg

Developed with ❤️ by [Enrico Sola](https://www.enricosola.dev).
