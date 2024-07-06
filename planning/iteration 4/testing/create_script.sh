#!/bin/bash

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"extension":"jar", "classFullName":"com.ilumusecase.scripts.App1"}' \
    "http://localhost:8080/projects/$2/job_nodes/$3/job_scripts"