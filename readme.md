# Digital Archive Server
Backend service that ...
* Calls out to Postgres DB for metadata about lore items
  e.g. http://0.0.0.0:8080/query?textsearch=ritual&folder=00001_05_Taiwan_folkspeech_1of2.pdf
* Calls out to S3 for PDF downloads
  e.g. http://0.0.0.0:8080/00001_05_Taiwan_folkspeech_1of2/5.pdf

# Stack
* Dockerizable Ktor service with Flyway to create Postgres tables, which are queried via code
* AWS S3 used for PDF storage and downloads

# Project Organization
* Project is organized into Application, Domain, and Persistence layers:
  * Application: routes and relevant logic for accessing lore
  * Domain: logical modelling of lore as needed for service uses
  * Persistence: DB-specific modelling of lore for posterity

# How to Run
Most easily run using IntelliJ. Must set up with AWS credentials, either using AWS plugins in IntelliJ or ENV variables

Set environment variable POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DATABASE in run config

Use psql to create user with password and database before trying to run flyway

To create docker image, use the docker-compose.yml file

* `create user test with password 'test123';`
## Create JAR for Docker Image using IntelliJ
File -> Project Structure -> Project Settings -> Artifacts -> Click + (plus sign) -> Jar -> From modules with dependencies...

see: https://stackoverflow.com/questions/1082580/how-to-build-jars-from-intellij-idea-properly

##
TODO: env specific configs
TODO: fix connection to postgres in docker