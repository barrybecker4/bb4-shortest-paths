#!/bin/bash

# In build.gradle, specify that as the main class.
echo "Building and unzipping executable "
./gradlew

# that will create bb4-discrete-optimization-1.8-SNAPSHOT.zip in /build/distributions dir

pushd build/distributions
rm -rf bb4-discrete-optimization-1.8.SNAPSHOT
# The "A" is to repond to the unzip prompt
/bin/echo "A" | unzip bb4-discrete-optimization-1.8-SNAPSHOT.zip
popd
# to test, run ./bb4-discrete-optimization in build directory.