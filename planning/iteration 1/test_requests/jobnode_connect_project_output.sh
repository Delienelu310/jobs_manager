#/bin/bash

curl -X PUT \
"localhost:8080/projects/$1/job_nodes/connect?output_job_node_id=$2&output_label=$3&project_output_label=$4"