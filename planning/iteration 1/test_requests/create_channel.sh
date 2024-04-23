#!/bin/bash

curl -X POST \
 -H "Content-Type: application/json" \
  -d '{"name":"somename", "type": "sometype"}' \
  "localhost:8080/projects/$1/channels"