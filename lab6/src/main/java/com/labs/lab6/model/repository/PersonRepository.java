package com.labs.lab6.model.repository;

import com.labs.lab6.model.Person;
import org.springframework.data.repository.CrudRepository;


public interface PersonRepository extends CrudRepository<Person, Long> {}
