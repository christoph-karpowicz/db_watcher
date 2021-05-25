#!/bin/bash

if [ $1 = "test-orcl" ]; then
    mvn clean test -DtestConfigPath="./config/orcl-example.yml"
elif [ $1 = "test-postgres" ]; then
    mvn clean test -DtestConfigPath="./config/postgres-example.yml"
elif [ $1 = "compile" ]; then
    mvn package shade:shade -DskipTests=true
else
    java -Xmx25m -cp target/dbw-1.1.0.jar com.dbw.app.Dbw "$@"
fi
