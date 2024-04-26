#/bin/bash

curl -X PUT \
"localhost:8080/projects/$1/job_nodes/connect?input_job_node_id=$2&input_label=$3&project_input_label=$4"