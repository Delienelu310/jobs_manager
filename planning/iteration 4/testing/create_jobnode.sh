#!/bin/bash

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename"}' \
    "localhost:5000/projects/$2/job_nodes"