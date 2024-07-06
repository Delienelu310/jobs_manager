#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"description": "somedescription"}' \
    "localhost:8080/projects/$2/job_nodes/$3/job_scripts/$4/job_entities"