#!/bin/bash

curl -H "Authorization: Bearer $1" -X GET "localhost:8080/projects"