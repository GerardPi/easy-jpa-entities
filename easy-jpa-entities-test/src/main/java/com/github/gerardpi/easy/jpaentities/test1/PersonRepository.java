package com.github.gerardpi.easy.jpaentities.test1;

import org.springframework.data.repository.PagingAndSortingRepository;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

public interface PersonRepository extends PagingAndSortingRepository<Person, UUID> {
}
