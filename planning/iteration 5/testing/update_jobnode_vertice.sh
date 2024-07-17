#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X PUT \
    -H "Authorization: Basic $1" \
    -d '{"x": 300, "y": 50}' \
    -H "Content-Type: application/json" \
    "localhost:8080/projects/$2/job_nodes/$3/graph"