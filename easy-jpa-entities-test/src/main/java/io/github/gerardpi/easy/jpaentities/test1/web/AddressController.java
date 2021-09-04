package io.github.gerardpi.easy.jpaentities.test1.web;

import io.github.gerardpi.easy.jpaentities.test1.UuidGenerator;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.Address;
import io.github.gerardpi.easy.jpaentities.test1.domain.addressbook.AddressRepository;
import io.github.gerardpi.easy.jpaentities.test1.web.addressbook.AddressDto;
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
@RequestMapping(AddressController.URI)
public class AddressController {
    public static final String URI = "/api/addresses";

    private static final Function<Address, AddressDto> TO_DTO = address -> AddressDto.from(address).build();
    private final UuidGenerator uuidGenerator;
    private final AddressRepository personRepository;

    public AddressController(final UuidGenerator uuidGenerator, final AddressRepository personRepository) {
        this.uuidGenerator = uuidGenerator;
        this.personRepository = personRepository;
    }

    @PostMapping
    public HttpEntity<Void> createAddress(@RequestBody final AddressDto addressDto) {
        final Address newAddress = addressDto.toEntity(Address.create(uuidGenerator.generate()).build()).build();
        final Address savedAddress = personRepository.save(newAddress);
        return ResponseEntity.ok()
                .eTag("" + savedAddress.getEtag())
                .location(toUri(URI, savedAddress.getId().toString()))
                .build();
    }

    /**
     * Note that, if a field is missing from the DTO, it will not be changed.
     */
    @PatchMapping
    public HttpEntity<Void> partiallyUpdateAddress(final UUID id, @RequestBody final AddressDto addressDto) {
        final Address existingAddress = personRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        final Address updatedAddress = addressDto.toEntityNotNull(existingAddress).build();
        final Address savedAddress = personRepository.save(updatedAddress);
        return ResponseEntity.ok()
                .eTag("" + savedAddress.getEtag())
                .location(toUri(URI, savedAddress.getId().toString()))
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
    public HttpEntity<Void> replaceAddress(final UUID id, @RequestBody final AddressDto addressDto) {
        final Address savedAddress = personRepository.save(addressDto.toEntity(
                personRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND))).build());
        return ResponseEntity.ok()
                .eTag("" + savedAddress.getEtag())
                .location(toUri(URI, savedAddress.getId().toString()))
                .build();
    }

    @GetMapping("/{id}")
    public HttpEntity<AddressDto> getAddress(@PathVariable final UUID id,
                                             @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) final Optional<String> ifNoneMatchHeader) {
        final AddressDto addressDto = getDtoForId(id);
        ControllerUtils.assertEtagDifferent(ifNoneMatchHeader, addressDto.getEtag(),
                toUri(URI, id.toString()).toString());
        return ControllerUtils.okResponse(addressDto);
    }

    private AddressDto getDtoForId(final UUID id) {
        return TO_DTO.apply(getById(id));
    }

    private Address getById(final UUID id) {
        return personRepository.findById(id)
                .orElseThrow(() -> ExceptionFactory.ENTITY_NOT_FOUND_BY_ID.apply(id, Address.class));
    }

    @GetMapping
    public Page<AddressDto> getAddresss(@PageableDefault(size = 10, sort = "name.last") final Pageable pageable) {
        return personRepository.findAll(pageable).map(TO_DTO);
    }

    @DeleteMapping
    public HttpEntity<Void> deleteAddress(@PathVariable final UUID id, @RequestHeader(value = HttpHeaders.IF_MATCH, required = false) final Integer expectedEtag) {
        final Address person = getById(id);
        ControllerUtils.assertEtagEqual(person, expectedEtag);
        personRepository.delete(person);
        return ControllerUtils.okNoContent();
    }
}
