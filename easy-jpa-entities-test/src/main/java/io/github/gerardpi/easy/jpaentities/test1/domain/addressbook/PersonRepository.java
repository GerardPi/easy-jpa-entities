package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

import io.github.gerardpi.easy.jpaentities.test1.web.ExceptionFactory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PersonRepository extends PagingAndSortingRepository<Person, UUID> {
    default Person getPersonById(final UUID id) {
        return this.findById(id)
                .orElseThrow(() -> ExceptionFactory.ENTITY_NOT_FOUND_BY_ID.apply(id, Person.class));
    }
}
