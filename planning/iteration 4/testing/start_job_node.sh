#!/bin/bash

curl -H "Authorization: Basic $1" \
    -X PUT "http://localhost:5000/projects/$2/job_nodes/$3/start"
    