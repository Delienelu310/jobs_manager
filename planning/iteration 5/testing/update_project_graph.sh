#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X PUT \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    "localhost:8080/projects/$2/graph"