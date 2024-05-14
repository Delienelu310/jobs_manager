#!/bin/bash

curl -H "Authorization: Basic YWRtaW46YWRtaW4="\
    -X PUT "localhost:8080/project/$1/privilege/remove/$2/$3"
