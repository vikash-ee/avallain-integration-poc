#!/bin/bash

# build jar
mvn clean install

# build docker image
docker build -t avallain-poc/hosting:0.0.1 .