package org.example.serialization;

import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import org.example.domain.Student;

import java.nio.ByteBuffer;
import java.util.Map;

public class StudentSerializer  implements Serializer<Student> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, Student student) {
        byte[] nameBytes = student.getFio().getBytes();
        int nameSize = nameBytes.length;
        ByteBuffer buffer = ByteBuffer.allocate(4 + nameSize + 4);
        buffer.putInt(nameSize);
        buffer.put(nameBytes);
        buffer.putInt(student.getId());
        return buffer.array();
    }

    @Override
    public byte[] serialize(String topic, Headers headers, Student data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
