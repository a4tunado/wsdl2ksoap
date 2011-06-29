#!/bin/sh

ROOT_PATH=$(dirname $0)
TEST_PATH=$ROOT_PATH/test
DIST_PATH=$ROOT_PATH/../wsdl2ksoap_test

WSDL2KSOAP=$ROOT_PATH/dist/WSDL2KSoap.jar

echo "Creating distanation directory..."
rm -rf $DIST_PATH
mkdir $DIST_PATH

for item in $TEST_PATH/*.wsdl; do

	echo "***** Processing $item..."

	base=$(basename $item .wsdl)
	
	echo "***** Generating sources..."	
	java -jar $WSDL2KSOAP file://$(readlink -f $item) $DIST_PATH/$base/src wsdl2ksoap.test.$base
	if [ $? != 0 ]; then exit $?; fi
	
	echo "***** Compiling..."
	javac -d $DIST_PATH/$base $DIST_PATH/$base/src/wsdl2ksoap/test/$base/*.java
	if [ $? != 0 ]; then exit $?; fi

done
