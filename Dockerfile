FROM maven:3.5.3-jdk-8-alpine as builder

WORKDIR /tmp/mvn

ADD ./ /tmp/mvn/
RUN mvn clean package -B

# application image

FROM java:8-alpine

LABEL maintainer="bakito <github@bakito.ch>"

EXPOSE 8080

RUN apk add --update --no-cache \
           graphviz \
           ttf-freefont

CMD java ${JAVA_OPTIONS} -jar /opt/crowd-trees/crowd-trees.jar

WORKDIR /opt/crowd-trees

COPY --from=builder /tmp/mvn/target/crowd-trees-*.jar /opt/crowd-trees/crowd-trees.jar

USER 1001




