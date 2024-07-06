#!/bin/bash

curl -H "Authorization: Basic $1" \
    -X DELETE "http://localhost:8080/projects/$2/job_nodes/$3/jobs_files/$4"