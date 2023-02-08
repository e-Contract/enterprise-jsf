#!/bin/bash

export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-11.jdk/Contents/Home"

if [ ! -d "$JAVA_HOME" ]
then
    echo "$JAVA_HOME not found"
    exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"
java -version
javac -version
