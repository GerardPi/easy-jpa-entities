package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonRepository;
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
    HttpEntity<Void> createPerson(PersonDto personDto) {
        Person person = fromDto(personDto);
        Person savedPerson = personRepository.save(person);
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getEtag())
                .location(java.net.URI.create(URI + "/" + savedPerson.getId()))
                .build();
    }

    @GetMapping("/{id}")
    HttpEntity<PersonDto> getPerson(@PathVariable UUID id) {
        return
                personRepository.findById(id)
                        .map(person -> ResponseEntity.ok()
                                .eTag("" + person.getEtag())
                                .body(this.toDto(person)))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    List<PersonDto> getPersons(Pageable pageable) {
        return
                personRepository.findAll(pageable).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList());
    }

    private PersonDto toDto(Person person) {
        return PersonDto.create().setDateOfBirth(person.getDateOfBirth()).setName(person.getName()).build();
    }

    private Person fromDto(PersonDto dto) {
        Person.Builder personBuilder = dto.isNew() ? Person.create(this.uuidGenerator.generate()) : Person.create(dto.getId());
        return personBuilder.setDateOfBirth(dto.getDateOfBirth()).setName(dto.getName()).build();
    }
}
