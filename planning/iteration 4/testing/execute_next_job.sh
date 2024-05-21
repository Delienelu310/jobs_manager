#!/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    "http://localhost:5000/projects/$2/job_nodes/$3/job/run_next"