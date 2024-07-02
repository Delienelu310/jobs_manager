#/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "type": "minio", "headers": ["number", "somedata"]}' \
    "localhost:8080/projects/$2/job_nodes/connect?input_job_node_id=$3&output_job_node_id=$4&input_label=$5&output_label=$6"