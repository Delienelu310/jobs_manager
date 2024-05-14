#!/bin/bash

curl -H "Authorization: Basic YWRtaW46YWRtaW4="\
    -X PUT "localhost:8080/project/$1/job_nodes/$2/privilege/add/$3/$4"
