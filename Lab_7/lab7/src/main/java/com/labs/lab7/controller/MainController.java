package com.labs.lab7.controller;

import com.labs.lab7.Config;
import com.labs.lab7.model.JsonResponse;
import com.labs.lab7.model.Person;
import com.labs.lab7.model.repository.PersonModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
public class MainController {

    @Autowired
    private PersonModelRepository personRepository;

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

}
