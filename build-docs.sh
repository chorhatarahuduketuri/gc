#!/usr/bin/env bash

if [ -z "$(command -v javadoc)" ];
then
	echo "Couldn't find javadoc installed. Exiting."
	exit 1
fi

echo "Please wait while I find your android path in /home/$USER/"

ANDROID_JAR=$(find ~ 2> /dev/null | grep "[0-9][0-9]/android.jar" | sort -u | tail -n 1)

if [ -n "$ANDROID_JAR" ]; then

	FILES=$(find app/src | grep "\.java")
	echo "$FILES" | awk -v P="$(pwd)" ' { printf "\"" P $1"" "\"\n" }  '

	javadoc -protected -splitindex -d "$PWD/docs" -classpath "$CLASSPATH" \
	       	-bootclasspath "$ANDROID_JAR" -Xdoclint:none
else
	echo "Couldn't find the Android path. Failed to build the docs :("	
	exit 2
fi
