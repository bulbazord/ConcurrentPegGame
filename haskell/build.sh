#!/bin/bash

if [[ $1 == "clean" ]]; then

    echo "Removing all generated files"
    rm ConcurrentPegGame
    rm ConcurrentPegGame.hi
    rm ConcurrentPegGame.o

elif [[ -z $1 ]]; then

    echo "Building..."
    ghc -threaded -O2 ConcurrentPegGame.hs 
else
    echo "Unkown commands: $@"
fi
