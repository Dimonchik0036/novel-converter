# Novel converter
Allows you to download chapters from [readlightnovel.org](https://www.readlightnovel.org)

# Build
```bash
./gradlew fatJar
```

# Usage
```
usage: [-h] TITLE [-l] [-d DESTINATION]

optional arguments:
  -h, --help                  show this help message and exit

  -l, --link                  Is a link

  -d DESTINATION,             Destination directory, default is current
  --destination DESTINATION   directory


positional arguments:
  TITLE                       The title part in the URL
```

# Example
Download all chapters
```bash
java -jar novel-converter-fatJar-0.1.jar tensei-shitara-slime-datta-ken-wn -d ~/Desktop
```

Download only one chapter
```bash
java -jar novel-converter-fatJar-0.1.jar -l no-game-no-life/volume-1/chapter-1
```