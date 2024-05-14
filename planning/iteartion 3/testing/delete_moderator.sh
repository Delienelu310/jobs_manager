#!/bin/bash

curl -H "Authorization: Basic YWRtaW46YWRtaW4="\
    -X DELETE\
    "localhost:8080/moderators/somename"