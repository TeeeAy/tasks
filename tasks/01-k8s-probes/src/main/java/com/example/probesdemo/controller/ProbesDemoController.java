package com.example.probesdemo.controller;

import com.example.probesdemo.entity.Person;
import com.example.probesdemo.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RefreshScope
public class ProbesDemoController {

    @Value("${app.message:default}")
    private String appMessage;

    private final PersonRepository personRepository;

    public ProbesDemoController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/")
    public String hello() {
        return appMessage;
    }

    @PostMapping("/people")
    public Person addPerson(@RequestBody Person p) {
        return personRepository.save(p);
    }

    @GetMapping("/people")
    public List<Person> allPeople() {
        return personRepository.findAll();
    }
}
