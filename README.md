# Haffman-with-MTF

##Build

```shell
./gradlew archiver-fatJar
```

artifact lie: `archiver/build/libs/archiver.jar`

##Run

```shell
java -jar archiver.jar encode file.txt result-file.txt
java -jar archiver.jar decode file.txt result-file.txt
```