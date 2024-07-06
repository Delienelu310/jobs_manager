#!/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    "localhost:8080/projects/$2/job_nodes/$3/ilum_groups/$4/start"