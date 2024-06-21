package com.labs.lab5.model.repository;

import com.labs.lab5.model.Person;
import org.springframework.data.repository.CrudRepository;


public interface PersonRepository extends CrudRepository<Person, Long> {}
