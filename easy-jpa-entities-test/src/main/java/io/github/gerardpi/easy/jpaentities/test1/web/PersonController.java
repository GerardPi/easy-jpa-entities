package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonRepository;
import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(PersonController.URI)
public class PersonController {
    public static final String URI = "/api/persons";
    private final UuidGenerator uuidGenerator;
    private final PersonRepository personRepository;

    public PersonController(UuidGenerator uuidGenerator, PersonRepository personRepository) {
        this.uuidGenerator = uuidGenerator;
        this.personRepository = personRepository;
    }

    @PostMapping
    HttpEntity<Void> createPerson(PersonDtoObsolete personDto) {
        Person person = personDto.toPerson(uuidGenerator.generate());
        Person savedPerson = personRepository.save(person);
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getOptLockVersion())
                .location(java.net.URI.create(URI + "/" + savedPerson.getId()))
                .build();
    }

    @GetMapping("/{id}")
    HttpEntity<PersonDtoObsolete> getPerson(@PathVariable UUID id) {
        return
                personRepository.findById(id)
                        .map(person -> ResponseEntity.ok().eTag("" + person.getOptLockVersion())
                                .body(PersonDtoObsolete.fromPerson(person)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    List<PersonDtoObsolete> getPersons(Pageable pageable) {
        return
                personRepository.findAll(pageable).stream()
                        .map(PersonDtoObsolete::fromPerson)
                        .collect(Collectors.toList());
    }
}
