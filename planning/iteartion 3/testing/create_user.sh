#!/bin/bash

#for admin:admin YWRtaW46YWRtaW4=
#for somename:pass - custom  user c29tZW5hbWU6cGFzcw==
#somename2:pass c29tZW5hbWUyOnBhc3M=
#somename3:pass c29tZW5hbWUzOnBhc3M=

curl -H "Authorization: Basic $1" \
    -H "Content-Type: application/json" \
    -d '{"username":"somename4", "password": "pass"}' \
    -X POST "localhost:8080/users?roles=WORKER" 