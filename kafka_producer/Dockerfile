FROM openjdk:8-jre-alpine
ARG JAR_FILE=target/kafka_producer*.jar
ENV KAFKA_SERVERS=localhost:9094
COPY ${JAR_FILE}  kafka_producer.jar
ENTRYPOINT ["java","-jar","/kafka_producer.jar"]