package io.github.gerardpi.easy.jpaentities.test1;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface PersonAddressRepository extends PagingAndSortingRepository<PersonAddress, UUID> {
    List<PersonAddress> findByAddressId(UUID addressId);

    List<PersonAddress> findByPersonId(UUID personId);
}
