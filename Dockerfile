FROM alpine/java:21-jdk
USER root

RUN mkdir -p /home/app

ENV HOME=/home/app
ENV APP=social-network-1.0.0.jar
ENV APP_HOME=/home/app/social-network
ENV APP_SOURCE=source

RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME

COPY src $APP_HOME/$APP_SOURCE/src
COPY mvnw $APP_HOME/$APP_SOURCE
COPY .mvn $APP_HOME/$APP_SOURCE/.mvn
COPY pom.xml $APP_HOME/$APP_SOURCE

WORKDIR $APP_HOME/$APP_SOURCE
RUN sh mvnw clean install

WORKDIR $APP_HOME
RUN cp $APP_SOURCE/target/social-network-1.0.0.jar $APP_HOME/$APP
ENTRYPOINT exec java -Djava.awt.headless=true -Dmail.mime.encodeparameters=false -jar $APP_HOME/$APP