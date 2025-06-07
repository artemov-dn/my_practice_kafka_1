package org.example.domain;

public class Student {
    private int id;
    private String fio;

    // Конструктор
    public Student(int id, String fio) {
        this.id = id;
        this.fio = fio;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    @Override
    public String toString() {
        return "id  = " + this.id + ", fio = '" + this.fio + "'";
    }
}
