# Overview
Transformer reads a CSV file and writes out a new CSV file with transformations applied. 
This app was written for my step-mom to help with some data-crunching.

# Installation
In order to run this application you must first build and package it from the source code.

The following tools are required to build & package the application:
* SBT: https://www.scala-sbt.org/download.html
* JDK8+: https://www.oracle.com/bh/java/technologies/javase/javase-jdk8-downloads.html

Once the tools are installed, follow these steps to build and package:
1. Download this source code repository (either using 'git clone' or download as a zip file and extract the archive).
2. Switch to the transformer directory and build, test and stage the application:
```
$ cd transformer
$ sbt clean test stage
```

# Configuration
See the following example configuration:
```
org.lowfi.transformer {
  transformations {
    "Groups Included" = Split
    Total = Divide
  }
}
```
The transformations object contains a map of header names to transformation types.
The following transformation types are supported:
* "split" - split a row into multiple rows, one for each comma-separated value within the cell.
* "divide" - when splitting a row, divide this cell's numeric values by number of rows in the split.

# Usage
After the build completes switch to stage directory and run the launcher script:
```
$ cd target/universal/stage
$ bin/transformer
```

See the program usage information printed to the console:
```
Usage: bin/transformer -Dconfig.file=<config-file> <input-file> <output-file>
```

Run the program again, specifying all the required arguments.
Note that the first line in the CSV file must contain the column headers.
```
$ bin/transformer -Dconfig.file=sample.conf sample.csv sample-transformed.csv
```
See the generated sample-transformed.csv file.