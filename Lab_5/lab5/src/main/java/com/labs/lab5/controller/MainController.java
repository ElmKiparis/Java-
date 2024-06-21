package com.labs.lab5.controller;

import com.labs.lab5.Config;
import com.labs.lab5.model.Person;
import com.labs.lab5.model.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
public class MainController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/")
    public String index(Model model) {

        Iterable<Person> persons = this.personRepository.findAll();

        model.addAttribute("static_files_url", Config.STATIC_FILES_URL);

        model.addAttribute("persons", persons);

        return "index";

    }

    @GetMapping("/person/add")
    public String addPersonDisplay() {
        return "add-person";
    }

    @PostMapping("/person/add")
    public String addPerson(@RequestPart(required = false) MultipartFile avatar, @RequestParam String name, @RequestParam int age) {

        String avatarFilename = null;

        if (avatar != null && !avatar.isEmpty()) {

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

                avatarFilename = fileName;

            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + avatar.getOriginalFilename(), e);
            }

        }

        Person person = new Person(name, age, avatarFilename);

        this.personRepository.save(person);

        return "redirect:/";

    }

    @GetMapping("/person/{id}/update")
    public String updatePersonDisplay(@PathVariable long id, Model model) {

        if (!this.personRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Person person = this.personRepository.findById(id).get();

        model.addAttribute("person", person);

        return "update-person";

    }

    @PostMapping("/person/{id}/update")
    public String updatePerson(@PathVariable long id, @RequestPart(required = false) MultipartFile avatar, @RequestParam String name, @RequestParam int age) {

        if (!this.personRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Person person = this.personRepository.findById(id).get();

        if (avatar != null && !avatar.isEmpty()) {

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

            } catch (IOException e) {
                throw new RuntimeException("Failed to store file " + avatar.getOriginalFilename(), e);
            }

        }

        person.setName(name);
        person.setAge(age);

        this.personRepository.save(person);

        return "redirect:/";

    }

    @GetMapping("/person/{id}/delete")
    public String deletePerson(@PathVariable long id) {

        Person person = this.personRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found")
        );

        this.personRepository.delete(person);

        return "redirect:/";

    }

}
