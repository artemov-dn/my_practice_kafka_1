## Описание проекта

Проект состоит из одного продюсера и двух типов консьюмеров для Apache Kafka.  
Продюсер отправляет сообщения в топик, а консьюмеры читают их. Один по обному сообщению с автоматическим коммитом оффсет.
Второй консумер считывает минимум по 10 сообщений и коммитит оффсет после обработки пачки.

### Классы и структура:
- `kafka_producer\src\main\java\org\example\Main.java` – продюсер, отправляющий сообщения в Kafka-топик.
- `kafka_producer\src\main\java\org\example\domain\Student.java` – класс `Student` описывающий сообщение, отправляемые в Kafka.
- `kafka_producer\src\main\java\org\example\serialization\StudentSerializer.java` – класс сериализации сообщений `Student` массив байтов.
- `kafka_consumer\src\main\java\org\example\Main.java` – консьюмер, обрабатывающий по одному сообщению (SingleMessageConsumer).
- `kafka_consumer\src\main\java\org\example\domain\Student.java` – класс `Student` описывающий сообщения, вычитываемые из Kafka.
- `kafka_consumer\src\main\java\org\example\serialization\StudentDeserializer.java` – класс десериализации сообщений из массива байтов в `Student`.
- `kafka_consumer_batch\src\main\java\org\example\Main.java` – консьюмер, обрабатывающий пачками от 10 сообщений (BatchMessageConsumer).
- `kafka_consumer_batch\src\main\java\org\example\domain\Student.java` – класс `Student` описывающий сообщения, вычитываемые из Kafka.
- `kafka_consumer_batch\src\main\java\org\example\serialization\StudentDeserializer.java` – класс десериализации сообщений из массива байтов в `Student`.

## Инструкция по запуску

1. Убедитесь, что у вас установлен `docker` и `docker-compose`.
2. Для запуска кластера Kafka необходимо перейти в папку kafka_cluster и выполнить команду:
    ```
    docker-compose up -d
    ```
3. Необходимо подключится к брокеру
    ```
    docker exec -it <имя контейнера с kafka> /bin/sh
    ```
4. B консоли создать топик 
    ```
	cd /opt/bitnami/kafka
    bin/kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 2
    ```
5. Убедится, что топик создан, вернутся в корнеевую папку проекта и запустить приложение выполнив команду
    ```
    docker-compose up -d
    ```

6. Сервисы, которые поднимутся:
    - Кластер Kafka на основе Bitnami образов (`kafka-0`, `kafka-1`, `kafka-2`).
    - Kafka UI для визуализации сообщений.
    - Приложение-продюсер.
    - Два инстанса SingleMessageConsumer.
    - Два инстанса BatchMessageConsumer.

4. Откройте браузер и перейдите на `http://localhost:8080`, чтобы зайти в Kafka UI и увидеть сообщения.

## Принцип работы

- Продюсер каждые 0,1 секунды отправляет новые сообщения в топик `test-topic`.
- SingleMessageConsumer:
    - Читает по одному сообщению.
    - Автоматически коммитит оффсеты.
- BatchMessageConsumer:
    - Читает минимум по 10 сообщений за раз.
    - Обрабатывает их в цикле.
    - После обработки пачки вручную коммитит оффсет.

## Проверка задания

- Сообщения регулярно появляются в Kafka UI в топике `test-topic`.
- Продюсер пишет сообщения без ошибок.
- SingleMessageConsumer обрабатывает сообщения по одному.
- BatchMessageConsumer обрабатывает минимум 10 сообщений за раз.
- Все приложения продолжают работать даже при временных ошибках сети.
- Используется сериализация и десериализация класса `Student`.
- Настроены гарантии доставки в продюсере (`acks=all`, `retries > 0`).

---
