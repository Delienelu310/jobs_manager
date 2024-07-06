#!/bin/bash

curl -H "Authorization: Basic $1" \
    -X POST "http://localhost:8080/projects/$2/job_nodes/$3/jobs_files" \
    --form 'files=@"intermidiate-1.0.jar"' \
    --form 'extension="jar"'\
    --form 'jobs_details={"name":"name","description":"default"};type=application/json'