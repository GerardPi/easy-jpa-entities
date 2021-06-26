package io.github.gerardpi.easy.jpaentities.test1.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends PagingAndSortingRepository<Address, UUID> {
    Optional<Address> findByCountryCodeAndPostalCodeAndHouseNumber(String contryCode, String postalCode, String houseNumber);
}
