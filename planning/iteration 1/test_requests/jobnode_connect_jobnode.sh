#/bin/bash

curl -X PUT \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "type": "sometype"}' \
    "localhost:8080/projects/$1/job_nodes/connect?input_job_node_id=$2&output_job_node_id=$3&input_label=$4&output_label=$5"