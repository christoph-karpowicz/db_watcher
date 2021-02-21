#!/bin/bash

if [ $1 = "test-orcl" ]; then
    mvn clean test -DtestConfigPath="./config/orcl-example.yml"
elif [ $1 = "test-postgres" ]; then
    mvn clean test -DtestConfigPath="./config/postgres-example.yml"
elif [ $1 = "compile" ]; then
    mvn package shade:shade -DskipTests=true
else
    if `grep -q .jar <<< "$1"`; then
        java -Xmx25m -cp $1:target/dbw-1.0.0.jar com.dbw.app.Dbw "${@:2}"
    else
        java -Xmx25m -cp target/dbw-1.0.0.jar com.dbw.app.Dbw "$@"
    fi
fi
