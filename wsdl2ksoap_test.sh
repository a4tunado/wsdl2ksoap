#!/bin/sh

ROOT_PATH=$(dirname $0)
TEST_PATH=$ROOT_PATH/test
DEST_PATH=$ROOT_PATH/../wsdl2ksoap_test

WSDL2KSOAP=$ROOT_PATH/dist/WSDL2KSoap.jar

echo "Creating destanation directory..."
#rm -rf $DEST_PATH
mkdir $DEST_PATH

for item in $TEST_PATH/*.wsdl; do

	echo "***** Processing $item..."

	base=$(basename $item .wsdl)
	
	echo "***** Generating sources..."	
	java -jar $WSDL2KSOAP file://$(readlink -f $item) $DEST_PATH/$base/src wsdl2ksoap.test.$base
	if [ $? != 0 ]; then exit $?; fi
	
	echo "***** Compiling..."
	javac -d $DEST_PATH/$base -cp $ROOT_PATH/libs/*.jar  $DEST_PATH/$base/src/wsdl2ksoap/test/$base/*.java
	if [ $? != 0 ]; then exit $?; fi

done
