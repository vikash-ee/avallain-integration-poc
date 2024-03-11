# Prerequisites

### Build Learnosity Java SDK

```
git clone https://github.com/Learnosity/learnosity-sdk-java
cd learnosity-sdk-java/
mvn clean install
```

### Build hosting app

```
cd hosting/
mvn clean install
cd ..
```

# Run the application

## Run inside Docker

### Copy the engines in engines/

_'engines/flare_engine'_
_'engines/sie_engine'_

### Start

```
cd hosting/
sh scripts/build.sh
cd ..
docker-compose up -d
```

## Run locally

### Start static server

```
cd static/
npm i -g http-server
http-server -a localhost -p 8000
```

Put the _flare_engine_ and _sie_engine_ folders inside static/

### Start LRS server

```
docker run \
    -it \
    -p 8080:8080 \
    -e LRSQL_API_KEY_DEFAULT=my_key \
    -e LRSQL_API_SECRET_DEFAULT=my_secret \
    -e LRSQL_ADMIN_USER_DEFAULT=my_username \
    -e LRSQL_ADMIN_PASS_DEFAULT=my_password \
    -e LRSQL_DB_HOST=avallaintest.postgres.database.azure.com \
    -e LRSQL_DB_PORT=5432 \
    -e LRSQL_DB_NAME=avallainadmin \
    -e LRSQL_DB_USER=avallaintest \
    -e LRSQL_DB_PASSWORD=Password@123 \
    -e LRSQL_ALLOW_ALL_ORIGINS=true \
    yetanalytics/lrsql:latest \
    /lrsql/bin/run_postgres.sh
```

### Start hosting service

Start the spring boot application (HostingApplication.java)
OR

```
cd hosting/
mvn clean install
java -jar target/hosting-0.0.1.jar
```

# Sync Avallain resources

To sync the avallain resources, run these sync jobs:

1. http://localhost:9090/publishingjobs/sync
2. http://localhost:9090/structures/sync
