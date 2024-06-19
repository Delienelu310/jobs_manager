#!/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "type": "kafka", "headers":["number","somedata"]}' \
    "localhost:5000/projects/$2/input/add/$3"