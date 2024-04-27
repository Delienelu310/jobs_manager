#!/bin/bash

curl -X PUT \
 -H "Content-Type: application/json" \
  -d '{"name":"somename", "type": "kafka"}' \
  "localhost:8080/projects/$1/input/add/$2"