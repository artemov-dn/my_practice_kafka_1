package org.example.serialization;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.example.domain.Student;

import java.nio.ByteBuffer;
import java.util.Map;

public class StudentDeserializer implements Deserializer<Student> {

    @Override
    public Student deserialize(String s, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int nameSize = buffer.getInt();
        byte[] nameBytes = new byte[nameSize];
        buffer.get(nameBytes);
        String name = new String(nameBytes);
        int id = buffer.getInt();
        return new Student(id, name);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }

    @Override
    public Student deserialize(String topic, Headers headers, ByteBuffer data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }
}
