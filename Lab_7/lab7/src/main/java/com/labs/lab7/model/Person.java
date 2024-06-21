package com.labs.lab7.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    private String avatar_filename;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "person_skill",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;

    public Person(String name, int age, String avatar_filename) {
        this.name = name;
        this.age = age;
        this.avatar_filename = avatar_filename;
    }

}
