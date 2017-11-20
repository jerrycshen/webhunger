#!/bin/sh

programdir="."

num=$#
temp=$CLASSPATH
#setting libs path
libs=../lib/*
append(){
   temp=$temp":"$1
}
for file in $libs; do
    append $file
done


export CLASSPATH=$temp
java -classpath $CLASSPATH   me.shenchao.webhunger.core.CoreBootstrap