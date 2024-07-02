#!/bin/bash

curl -X PUT\
    -H "Authorization: Basic $1" \
    "localhost:8000/projects/$2/channels/$3/start"
