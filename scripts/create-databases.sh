#!/bin/bash

cd ..
echo "Starting docker container for MySQL..."
docker-compose up -d  mysql

sleep 5

CONTAINER_ID=$(docker ps -q --filter name=oioi-mysql)
echo "MySQL container started with ID: $CONTAINER_ID"

# Change dump file location to match your environment
docker cp ../db_dumps/sta-oioi-api.sql $CONTAINER_ID:/tmp/a.sql
echo "Copied database dump for API"

docker cp ../db_dumps/sta-oioi-kc.sql $CONTAINER_ID:/tmp/kc.sql
echo "Copied database dump for Keycloak"

echo "Creating databases with dump data..."
docker exec $CONTAINER_ID  mysql -uroot -proot -e 'DROP DATABASE IF EXISTS `keycloak`; DROP DATABASE IF EXISTS `oioi`; CREATE DATABASE `keycloak` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */; CREATE DATABASE `oioi` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */; USE oioi; source /tmp/a.sql; USE keycloak; source /tmp/kc.sql; commit;'

docker-compose down