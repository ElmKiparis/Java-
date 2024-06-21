package com.labs.lab5.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String avatar_filename;

    public Person() {}

    public Person(String name, int age, String avatar_filename) {

        this.name = name;
        this.age = age;
        this.avatar_filename = avatar_filename;

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatar_filename() {
        return this.avatar_filename;
    }

    public void setAvatar_filename(String avatar_filename) {
        this.avatar_filename = avatar_filename;
    }

}
