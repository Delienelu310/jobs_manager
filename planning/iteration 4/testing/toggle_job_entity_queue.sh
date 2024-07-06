#!/bin/bash

# token: YWRtaW46YWRtaW4=

curl -X PUT \
    -H "Authorization: Basic $1" \
    "localhost:8080/projects/$2/job_nodes/$3/toggle/test_queue/$4"