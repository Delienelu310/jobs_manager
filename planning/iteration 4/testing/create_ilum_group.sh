#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"maxJobDuration": 1000}' \
    "localhost:8080/projects/$2/job_nodes/$3/ilum_group"