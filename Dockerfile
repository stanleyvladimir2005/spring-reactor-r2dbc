FROM openjdk:21-slim

LABEL author=stanleyvladimir2005@gmail.com

COPY "./build/libs/spring-reactor-r2dbc-0.0.1-SNAPSHOT.jar" "app.jar"

ENTRYPOINT ["java", "-jar", "/app.jar"]