FROM openjdk:8-jre-alpine

ADD build/libs/game-of-life.jar game-of-life.jar

RUN sh -c 'touch /game-of-life.jar'

EXPOSE 8080

HEALTHCHECK CMD wget -s http://localhost:8080/health || exit 1

CMD java -jar game-of-life.jar
