#!/bin/bash

curl -H "Authorization: Basic $1" -X PUT  "localhost:5000/projects/$2/channels/$3/stop"
