#!/bin/bash

curl -X PUT \
    "localhost:8080/projects/$1/job_nodes/$2/add/input/$3"