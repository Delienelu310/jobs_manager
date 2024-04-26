#!/bin/bash

curl -X POST \
 -H "Content-Type: application/json" \
  -d '{"name":"somename"}' \
  "localhost:8080/projects/$1/job_nodes"