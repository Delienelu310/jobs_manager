#!/bin/bash

#for admin:admin YWRtaW46YWRtaW4=
#for somename:pass - custom  user c29tZW5hbWU6cGFzcw==
#somename2:pass c29tZW5hbWV3OnBhc3M=

curl -H "Authorization: Basic $1" "localhost:5000/projects"