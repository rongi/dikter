## Generates java classes and transformation methods from json

## Install

```
brew install --HEAD rongi/tap/dikter
```

## Update

```
brew reinstall --HEAD rongi/tap/dikter
```

## Usage

`dikter <json_file> <path_where_to_generate> <domain_package> <entity_package> <transform_package>`

### With dikter.properties file

Instead of specifying package names in the command line you can add them to the file named dikter.properties and put that file to the project root. Then you'll need to specify only the json file. Like this:

`dikter <json_file>`

### Init properties

`dikter --init`

Creates 'dikter.properties' file in the current directory

## Build

./gradlew run - runs script

./gradlew assembleDist - Assembles the main distributions (archives)

./gradlew installDist - Assembles the main distributions (exploded)

./gradlew run -PappArgs="['src/test/resources/test.json']" - Runs script with parameters
