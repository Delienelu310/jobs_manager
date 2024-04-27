#!/bin/bash

curl -X PUT "localhost:8080/projects/$1/channels/$2/start"
