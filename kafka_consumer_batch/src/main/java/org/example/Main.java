package org.example;

import static java.lang.Thread.sleep;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.RecordDeserializationException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.example.domain.Student;
import org.example.serialization.StudentDeserializer;
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
        LOG.info("Env CONSUMER_TIMEOUT: {}", System.getenv("CONSUMER_TIMEOUT"));
        int timeout;
        try {
            timeout = Integer.parseInt(System.getenv("CONSUMER_TIMEOUT"));
        } catch (NumberFormatException e) {
            timeout = 5000;
        }
        if (timeout < 0){
            timeout = 0;
        }
        
        // Конфигурация продюсера – адрес сервера, сериализаторы для ключа и значения.
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-batch-group");        // Уникальный идентификатор группы
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StudentDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");         // Начало чтения с самого начала
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");           // Автоматический коммит смещений отключен
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");           // Время ожидания активности от консьюмера
        properties.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "5000");               // минимальный объём данных (в байтах)
        properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "5000");             // максимальное время ожидания для получения данных
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "20");                // максимальный размер пакета
        // Создание консьюмера
        try(KafkaConsumer<String, Student> consumer = new KafkaConsumer<>(properties)) {
            // Подписка на топик
            consumer.subscribe(Collections.singletonList(topic));
            ArrayList<Student> batch = new ArrayList<>();
            // Чтение сообщений в бесконечном цикле
            while (true) {
                try {
                    ConsumerRecords<String, Student> records = consumer.poll(Duration.ofMillis(timeout));  // Получение сообщений
                    LOG.info("Received {} messages", records.count());
                    for (ConsumerRecord<String, Student> record : records) {
                        batch.add(record.value());
                    }
                    if (batch.size() >= 10) {
                        for (Student student : batch) {
                            LOG.info("Received message: Student{{}} ", student.toString());
                        }
                        batch = new ArrayList<>();
                        consumer.commitSync();
                    }
                }catch (RecordDeserializationException e){
                    LOG.error("Error deserializing message!");
                } catch (Exception e) {
                    LOG.error("Error get message from kafka!");
                }
            }
        }
    }
}