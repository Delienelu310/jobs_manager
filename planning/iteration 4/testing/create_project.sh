#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "description": "somedescription"}' \
    "localhost:8080/projects"