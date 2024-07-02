#!/bin/bash

curl -H "Authorization: Basic $1" \
    -X POST "http://localhost:8080/projects/$2/job_nodes/$3/jobs" \
    --form 'files=@"intermidiate-1.0-jar-with-dependencies.jar"' \
    --form 'name="somename"' \
    --form 'description="default"' \
    --form 'job_classes="com.ilumusecase.scripts.App1,com.ilumusecase.scripts.App2,com.ilumusecase.scripts.App3"'