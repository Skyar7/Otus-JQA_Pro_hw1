#!/usr/bin/env bash

command="mvn test -Dbrowser.name=$BROWSER_NAME -Dbrowser.version=$BROWSER_VERSION -Dbase.url=$BASE_URL -Dremote.url=$REMOTE_URL -Ddriver.type=$DRIVER_TYPE"

echo "Run: " $command
eval $command
echo "End of running: " $command