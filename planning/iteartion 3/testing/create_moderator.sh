#!/bin/bash

# for admin:admin credentials
curl -H "Authorization: Basic YWRtaW46YWRtaW4=" \
    -H "Content-Type: application/json" \
    -d '{"username":"somename", "password": "pass"}' \
    -X POST "localhost:8080/moderators" 