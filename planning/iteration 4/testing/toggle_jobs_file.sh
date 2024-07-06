#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X PUT \
    -H "Authorization: Basic $1" \
    "localhost:8080/projects/$2/job_nodes/$3/job_scripts/$4/toggle/jobs_files/$5"