FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/storage-0.1.0.jar $PROJECT_HOME/storage-0.1.0.jar

WORKDIR $PROJECT_HOME

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.3.0/wait /wait
RUN chmod +x /wait

CMD /wait && java  -Dspring.data.mongodb.uri=mongodb://mongodb:27017 -Djava.security.egd=file:/dev/./urandom -jar ./storage-0.1.0.jar
