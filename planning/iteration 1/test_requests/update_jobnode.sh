#!/bin/bash

curl -X PUT \
    -H "Content-Type: application/json" \
    -d '{"name":"new name"}' \
    "localhost:8080/projects/$1/job_nodes/$2"