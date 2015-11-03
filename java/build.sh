#!/bin/bash

if [[ $1 == "clean" ]]; then

    echo "Removing all generated files"
    rm *.class

elif [[ -z $1 ]]; then

    echo "Building..."
    javac ConcurrentPegGame.java 
else
    echo "Unkown commands: $@"
fi
