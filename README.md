## Generates java classes and transformation methods from json

# Install

```
brew tap rongi/tap
brew install dikter
```

## Usage

`dikter <json_file> <path_where_to_generate> <domain_package> <entity_package> <transform_package>`

or if you have file dikter.properties in the working directory 
with properties: src, domainPackage, entityPackage, then you need to point
only to the json file. Like this:

`dikter <json_file>`

`dikter --init`

Creates properties file

## Build

./gradlew run - runs script

./gradlew assembleDist - Assembles the main distributions (archives)

./gradlew installDist - Assembles the main distributions (exploded)

./gradlew run -PappArgs="['src/test/resources/test.json']" - Runs script with parameters
