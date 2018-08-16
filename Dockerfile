FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY twitter-crawler/target/twitter-crawler-0.1.0.jar $PROJECT_HOME/twitter-crawler-0.1.0.jar

WORKDIR $PROJECT_HOME

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.3.0/wait /wait
RUN chmod +x /wait

CMD /wait && java -Dspring.data.mongodb.uri=mongodb://mongodb:27017 -Djava.security.egd=file:/dev/./urandom -jar ./twitter-crawler-0.1.0.jar
