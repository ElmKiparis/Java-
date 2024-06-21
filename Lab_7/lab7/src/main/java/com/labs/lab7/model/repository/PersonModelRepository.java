package com.labs.lab7.model.repository;

import com.labs.lab7.model.Person;
import org.springframework.data.repository.CrudRepository;


public interface PersonModelRepository extends CrudRepository<Person, Long> {}
