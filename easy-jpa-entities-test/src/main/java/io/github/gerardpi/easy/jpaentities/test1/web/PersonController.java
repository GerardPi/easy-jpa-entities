package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Person;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.PersonRepository;
import io.github.gerardpi.easy.jpaentities.test1.web.addressbook.PersonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static io.github.gerardpi.easy.jpaentities.test1.web.ControllerUtils.toUri;

@RestController
@RequestMapping(PersonController.URI)
public class PersonController {
    public static final String URI = "/api/persons";
    private static final Function<Person, PersonDto> TO_DTO = person -> PersonDto
            .from(person)
            .setDateOfBirth(person.getDateOfBirth())
            .setName(person.getName()).build();
    private final UuidGenerator uuidGenerator;
    private final PersonRepository personRepository;

    public PersonController(final UuidGenerator uuidGenerator, final PersonRepository personRepository) {
        this.uuidGenerator = uuidGenerator;
        this.personRepository = personRepository;
    }

    @PostMapping
    public HttpEntity<Void> createPerson(@RequestBody final PersonDto personDto) {
        final Person person = personDto.toEntity(Person.create(uuidGenerator.generate()).build()).build();
        final Person savedPerson = personRepository.save(person);
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getEtag())
                .location(toUri(URI, savedPerson.getId().toString()))
                .build();
    }

    /**
     * Note that, if a field is missing from the DTO, it will not be changed.
     */
    @PatchMapping
    public HttpEntity<Void> partiallyUpdatePerson(final UUID id, @RequestBody final PersonDto personDto) {
        final Person existingPerson = personRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final Person updatedPerson = personDto.toEntityNotNull(existingPerson).build();
        final Person savedPerson = personRepository.save(updatedPerson);
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getEtag())
                .location(toUri(URI, savedPerson.getId().toString()))
                .build();
    }

    /**
     * An update can only be used to overwrite an existing entity.
     * <p>
     * In theory, it could be used to create an entity, but that would imply that the ID is known by the client up-front.
     * Since the ID is always generated server-side, that is not allowed.
     * <p>
     * Using a PUT to replace is idempotent, meaning that all fields will be overwritten.
     */
    @PutMapping
    public HttpEntity<Void> replacePerson(final UUID id, @RequestBody final PersonDto personDto) {
        final Person savedPerson = personRepository.save(personDto.toEntity(personRepository.getPersonById(id)).build());
        return ResponseEntity.ok()
                .eTag("" + savedPerson.getEtag())
                .location(toUri(URI, savedPerson.getId().toString()))
                .build();
    }

    @GetMapping("/{id}")
    public HttpEntity<PersonDto> getPerson(
            @PathVariable final UUID id,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) final Optional<String> ifNoneMatchHeader) {
        final PersonDto personDto = getDtoForId(id);
        ControllerUtils.assertEtagDifferent(ifNoneMatchHeader, personDto.getEtag(),
                toUri(URI, id.toString()).toString());
        return ControllerUtils.okResponse(personDto);
    }

    private PersonDto getDtoForId(final UUID id) {
        return TO_DTO.apply(getById(id));
    }

    private Person getById(final UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> ExceptionFactory.ENTITY_NOT_FOUND_BY_ID.apply(id, Person.class));
    }

    @GetMapping
    public Page<PersonDto> getPersons(@PageableDefault(size = 10, sort = "name.last") final Pageable pageable) {
        return personRepository.findAll(pageable).map(TO_DTO);
    }

    @DeleteMapping
    public HttpEntity<Void> deletePerson(
            @PathVariable final UUID id,
            @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) final Integer expectedEtag) {
        final Person person = getById(id);
        ControllerUtils.assertEtagEqual(person, expectedEtag);
        personRepository.delete(person);
        return ControllerUtils.okNoContent();
    }
}
