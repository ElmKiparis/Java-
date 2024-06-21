package com.labs.lab7.repository;

import com.labs.lab7.model.Person;
import com.labs.lab7.model.Skill;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;


@Projection(name = "persons", types = { Person.class })
public interface PersonWithSkills {

    Long getId();
    String getName();
    Integer getAge();
    String getAvatar_filename();
    Set<Skill> getSkills();

}
