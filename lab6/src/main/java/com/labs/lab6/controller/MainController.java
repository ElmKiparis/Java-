package com.labs.lab6.controller;

import com.labs.lab6.Config;
import com.labs.lab6.model.JsonResponse;
import com.labs.lab6.model.Person;
import com.labs.lab6.model.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@RestController
public class MainController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/persons")
    public List<Person> getPersons(Model model) {

        Iterable<Person> persons = this.personRepository.findAll();

        return (List<Person>) persons;

    }

    @PostMapping("/person/add")
    public JsonResponse addPerson(@RequestParam String name, @RequestParam int age) {

        Person person = new Person(name, age);

        this.personRepository.save(person);

        return new JsonResponse(true, "Person added successfully");

    }

    @PostMapping("/person/{id}/upload")
    public JsonResponse uploadAvatar(@RequestPart MultipartFile avatar, @PathVariable long id) {

        if (!this.personRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Person person = this.personRepository.findById(id).get();

        Path rootLocation = Paths.get(Config.STATIC_FILES_PATH);

        try {

            String extension = "";
            String originalFilename = avatar.getOriginalFilename();
            int i = originalFilename.lastIndexOf('.');

            if (i > 0) {
                extension = originalFilename.substring(i + 1);
            }

            String fileName = java.util.UUID.randomUUID() + "." + extension;

            Files.copy(avatar.getInputStream(), rootLocation.resolve(fileName));

            person.setAvatar_filename(fileName);

            this.personRepository.save(person);

            return new JsonResponse(true, "Avatar uploaded successfully");

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + avatar.getOriginalFilename(), e);
        }

    }

    @PutMapping("/person/{id}/update")
    public JsonResponse updatePerson(@PathVariable long id, @RequestParam String name, @RequestParam int age) {

        if (!this.personRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Person person = this.personRepository.findById(id).get();

        person.setName(name);
        person.setAge(age);

        this.personRepository.save(person);

        return new JsonResponse(true, "Person updated successfully");

    }

    @DeleteMapping("/person/{id}/delete")
    public JsonResponse deletePerson(@PathVariable long id) {

        Person person = this.personRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        this.personRepository.delete(person);

        return new JsonResponse(true, "Person deleted successfully");

    }

}
