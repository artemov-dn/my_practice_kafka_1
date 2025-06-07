package org.example;

import static java.lang.Thread.sleep;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

import org.example.domain.Student;
import org.example.serialization.StudentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    public static final Logger LOG = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOG.info("Producer started!");
        String servers = System.getenv("KAFKA_SERVERS");
        LOG.info("Env KAFKA_SERVERS: {}", servers);
        if (servers == null || servers.isEmpty()){
            servers = "localhost:29094,localhost:29095,localhost:29096";
        }

        String topic = System.getenv("KAFKA_TOPIC");
        LOG.info("Env KAFKA_TOPIC: {}", topic);
        if (topic == null || topic.isEmpty()){
            topic = "test-topic";
        }

        LOG.info("Env PRODUCER_TIMEOUT: {}", System.getenv("PRODUCER_TIMEOUT"));
        int timeout;
        try {
            timeout = Integer.parseInt(System.getenv("PRODUCER_TIMEOUT"));
        } catch (NumberFormatException e) {
            timeout = 100;
        }
        if (timeout < 0){
            timeout = 0;
        }
        
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StudentSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");                // Для синхронной репликации
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);                 // Количество попыток для переотправки

        // Создание продюсера
        try(KafkaProducer<String, Student> producer = new KafkaProducer<>(properties);) {
            int id = 1;
            while (true) {
                Student student = new Student(id, "Иванов Иван Иванович " + id);
                // Отправка сообщения
                LOG.info("Try send message - Student: {}", student);
                ProducerRecord<String, Student> record = new ProducerRecord<>(topic, UUID.randomUUID().toString(), student);
                try {
                    producer.send(record).get();
                    LOG.info("Success send message!");
                }catch (SerializationException e){
                    LOG.error("Error serializing message!");
                } catch (Exception e) {
                    LOG.error("Error send message to kafka!");
                }
                id++;
                try {
                    sleep(timeout);
                } catch (InterruptedException e) {
                    // Обработка исключения InterruptedException, если поток был прерван
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}