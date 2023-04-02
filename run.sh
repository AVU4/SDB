#!/bin/bash
mvn clean install -DskipTests
cd src/docker
docker compose build
docker compose up