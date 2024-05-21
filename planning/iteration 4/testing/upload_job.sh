#!/bin/bash

curl -H "Authorization: Basic $1" \
    -X POST "http://localhost:5000/projects/$2/job_nodes/$3/jobs" \
    --form 'files=@"script-1.0.jar"' \
    --form 'name="somename"' \
    --form 'description="default"'