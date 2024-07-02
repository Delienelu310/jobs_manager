#/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    "localhost:8080/projects/$2/job_nodes/connect?input_job_node_id=$3&input_label=$4&project_input_label=$5"