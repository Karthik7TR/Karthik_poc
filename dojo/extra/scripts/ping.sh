#!/bin/bash -e

# Pings the main page of the application and looks for the 'Version' string, if it exists.

# NOTE: This requires you to have a tunnel set up to your application that points to localhost:8080

# Usage: ./dojo/extras/scripts/ping.sh blue|green|live [count]
#   - blue|green|live determines which header to attach to the request
#   - count is the number of times to repeat the call (useful for looking at a linear shift)

# Examples:
# ./dojo/extra/scripts/ping.sh green
# ./dojo/extra/scripts/ping.sh blue
# ./dojo/extra/scripts/ping.sh live 20

HEADER="$1"
COUNT="$2"

shopt -s nocasematch
if [[ $HEADER == "blue" ]] ; then
    HEADER_ARG="Blue"
elif [[ $HEADER == "green" ]] ; then
    HEADER_ARG="Green"
elif [[ $HEADER == "live" ]] ; then
    HEADER_ARG="n/a" 
else
    echo "Expected first argument to be one of 'blue', 'green', or 'live'."
    exit 1
fi

if [ -z "${COUNT}" ] ; then
    COUNT=1
elif [[ "${COUNT}" -ge 1 ]] ; then
    : # do nothing
else
    echo "Expected second argument to be either empty or a positive integer."
    exit 1
fi

for i in $(seq ${COUNT}) ; do
    curl -H "X-BlueGreen-Routing: ${HEADER_ARG}" localhost:8080/ 2>&1 | grep -io 'version[^<]*'
    sleep 1
done
