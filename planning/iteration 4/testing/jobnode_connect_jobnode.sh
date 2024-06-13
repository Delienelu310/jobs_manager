#/bin/bash

curl -X PUT \
    -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"name":"somename", "type": "kafka"}' \
    "localhost:5000/projects/$2/job_nodes/connect?input_job_node_id=$3&output_job_node_id=$4&input_label=$5&output_label=$6"