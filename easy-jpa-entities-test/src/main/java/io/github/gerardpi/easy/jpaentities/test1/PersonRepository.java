package io.github.gerardpi.easy.jpaentities.test1;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface PersonRepository extends PagingAndSortingRepository<Person, UUID> {
}
