FROM openjdk:8-alpine

# Required for starting application up.
RUN apk update && apk add bash

RUN mkdir -p /opt/app
ENV PROJECT_HOME /opt/app

COPY target/minianalytics-api-0.1.0.jar $PROJECT_HOME/minianalytics-api-0.1.0.jar

WORKDIR $PROJECT_HOME

# Adding run script
ADD docker/* /
RUN chmod +x /run.sh

# Adding a script to wait for mongo to load
ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.3.0/wait /wait
RUN chmod +x /wait

#run script to start
CMD /run.sh
