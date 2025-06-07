FROM openjdk:8-jre-alpine
ARG JAR_FILE=target/kafka_consumer_batch*.jar
ENV KAFKA_SERVERS=localhost:9094
COPY ${JAR_FILE}  kafka_consumer_batch.jar
ENTRYPOINT ["java","-jar","/kafka_consumer_batch.jar"]