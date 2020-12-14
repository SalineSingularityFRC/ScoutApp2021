#!/bin/sh

FILE="app/src/main/java/com/scouting/ssss/MainActivity.kt"
VERSION=`cat version.txt || exit 1`
HASH=`git rev-parse --short HEAD`
FULL="${VERSION}-${HASH}"

FIRST=`cat ${FILE} | sed "s/{{VERSION}}/${FULL}/g"`
echo "${FIRST}" > "${FILE}"

./gradlew

SECOND=`cat app/src/main/java/com/scouting/ssss/MainActivity.kt | sed "s/${FULL}/{{VERSION}}/g"`
echo "${SECOND}" > "${FILE}"
