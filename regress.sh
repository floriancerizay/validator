#!/bin/sh

export VALIDATOR_PATH=$PWD/validator-cli/target/validator-cli-2.2-SNAPSHOT.jar
export LOG4J_PATH=$PWD/validator-core/log4j2.xml

report_diff(){
	echo "diff $1 $2"
	cat "$1" | grep "<Message>" > "$1.tmp"
	cat "$2" | grep "<Message>" > "$2.tmp"
	diff "$1.tmp" "$2.tmp"
	rm "$1.tmp" "$2.tmp"
}

csv_diff(){
	echo "diff $1 $2"
	diff "$1" "$2"
}


# build
mvn clean
mvn package

# run geofla
rm validator-example/geofla/data/*.csv
rm -rf validator-example/geofla/validation
java -Dlog4j.configurationFile=$LOG4J_PATH -jar $VALIDATOR_PATH -c validator-example/geofla/config/ -v GEOFLA_2015 -i validator-example/geofla/data -s EPSG:2154 -W LATIN1

echo "------------------------------------------------"
echo "-- check no diff"
echo "------------------------------------------------"
report_diff validator-example/geofla/validation-expected/validation.xml validator-example/geofla/validation/validation.xml
csv_diff validator-example/geofla/validation-expected/data/DATA/COMMUNE.csv validator-example/geofla/validation/data/DATA/COMMUNE.csv
csv_diff validator-example/geofla/validation-expected/data/DATA/LIMITE_COMMUNE.csv validator-example/geofla/validation/data/DATA/LIMITE_COMMUNE.csv
