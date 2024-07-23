#!/bin/bash

curl -X POST \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"extension":"jar", "classFullName":"com.ilumusecase.scripts.Tester", "jobScriptDetails": {"name": "somename"}}' \
    "http://localhost:8080/projects/$2/job_nodes/$3/job_scripts"