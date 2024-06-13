#!/bin/bash

curl -X PUT\
    -H "Authorization: Basic $1" \
    "localhost:5000/projects/$2/channels/$3/start"
