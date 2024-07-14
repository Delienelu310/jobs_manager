#!/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "type": "minio", "headers":["number","somedata"]}' \
    "localhost:8080/projects/$2/output/add/$3"