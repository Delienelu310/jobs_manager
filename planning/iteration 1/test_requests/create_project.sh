#!/bin/bash

curl -X POST \
 -H "Content-Type: application/json" \
  -d '{"name":"somename", "description": "somedescription"}' \
  "localhost:8080/projects"