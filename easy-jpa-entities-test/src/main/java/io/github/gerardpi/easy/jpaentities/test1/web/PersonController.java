package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import io.github.gerardpi.easy.jpaentities.test1.domain.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonName;
import io.github.gerardpi.easy.jpaentities.test1.domain.PersonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping(PersonController.URI)
public class PersonController {
    public static final String URI = "/api/persons";
    private static final Function<Person, PersonDto> TO_DTO = (person) -> PersonDto
            .from(person)
            .setDateOfBirth(person.getDateOfBirth())
            .setName(person.getName()).build();
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

    @PutMapping
    HttpEntity<Void> updatePerson(UUID id, PersonDto personDto) {
        Person existingPerson = personRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Person updatedPerson = copyUpdatesToPerson(personDto, existingPerson);
        Person savedPerson = personRepository.save(updatedPerson);
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getEtag())
                .location(java.net.URI.create(URI + "/" + savedPerson.getId()))
                .build();
    }

    @GetMapping("/{id}")
    HttpEntity<PersonDto> getPerson(@PathVariable UUID id) {
        return
                personRepository.findById(id)
                        .map(TO_DTO)
                        .map(dto -> ResponseEntity.ok()
                                .eTag(dto.getEtag())
                                .body(dto))
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    Page<PersonDto> getPersons(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Page<PersonDto> r = personRepository.findAll(PageRequest.of(page, pageSize)).map(TO_DTO);
        return r;
    }

    @GetMapping("/dus")
    Page<PersonDto> getPersons2(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize) {
        return new PageImpl<>(Collections.singletonList(
                PersonDto.create()
                        .setName(PersonName
                                .create()
                                .setLast("last")
                                .setFirst("first")
                                .build())
                        .setDateOfBirth(LocalDate.now())
                        .build()));
    }

    private Person fromDto(PersonDto dto) {
        Person.Builder personBuilder = dto.isNew() ? Person.create(this.uuidGenerator.generate()) : Person.create(dto.getId());
        return personBuilder.setDateOfBirth(dto.getDateOfBirth()).setName(dto.getName()).build();
    }

    private Person copyUpdatesToPerson(PersonDto dto, Person person) {
        return person.modify().setName(dto.getName()).setDateOfBirth(dto.getDateOfBirth()).build();
    }
}
