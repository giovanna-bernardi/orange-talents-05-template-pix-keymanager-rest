FROM openjdk:11
ARG JAR_FILE=build/libs/*-all.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENV APP_NAME keymanager-rest
ENTRYPOINT ["java","-jar","/app.jar"]
