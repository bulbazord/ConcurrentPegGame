#!/bin/bash

if [[ $1 == "clean" ]]; then

    echo "Removing all generated files"
    rm ConcurrentPegGame

elif [[ -z $1 ]]; then

    echo "Building..."
    go build ConcurrentPegGame.go
else
    echo "Unkown commands: $@"
fi
