#!/bin/sh

# argument parsing
case "$1" in
  "install") ACTION="./gradlew installDebug" ;; # install the app
  *) ACTION="./gradlew" ;; # otherwise just build
esac

# if $ACTION is somehow still unset, set it
[ -z "$ACTION" ] && ACTION="./gradlew"

# the file we're searching
FILE="app/src/main/java/com/scouting/ssss/MainActivity.kt"

# get the version from version.txt
# if it fails, exit
VERSION=`cat version.txt || exit 1`

# get the short git hash. something like "a5ad81d"
HASH=`git rev-parse --short HEAD`

# concatenate the version and the hash, something like "1.0.0-a5ad81d"
FULL="${VERSION}-${HASH}"

# get the initial contents of the file from sed
# capture this instead of echoing directly to the file to avoid a data race
FIRST=`cat ${FILE} | sed "s/{{VERSION}}/${FULL}/g"`
# write the updated file
echo "${FIRST}" > "${FILE}"

# build
${ACTION}

# re-insert the text {{VERSION}} for next time, again capturing rather than echoing
SECOND=`cat app/src/main/java/com/scouting/ssss/MainActivity.kt | sed "s/${FULL}/{{VERSION}}/g"`
# write the un-updated file
echo "${SECOND}" > "${FILE}"
