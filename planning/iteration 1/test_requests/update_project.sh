#!/bin/bash

curl -X PUT \
    -H "Content-Type: application/json" \
    -d '{"name":"new name", "description": "new description"}' \
    "localhost:8080/projects/$1"