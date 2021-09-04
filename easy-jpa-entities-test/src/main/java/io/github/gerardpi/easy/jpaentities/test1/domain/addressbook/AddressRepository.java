package io.github.gerardpi.easy.jpaentities.test1.domain.addressbook;

import io.github.gerardpi.easy.jpaentities.test1.web.ExceptionFactory;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends PagingAndSortingRepository<Address, UUID> {
    Optional<Address> findByCountryCodeAndPostalCodeAndHouseNumber(String contryCode, String postalCode, String houseNumber);

    default Address getAddressById(final UUID id) {
        return this.findById(id)
                .orElseThrow(() -> ExceptionFactory.ENTITY_NOT_FOUND_BY_ID.apply(id, Address.class));
    }
}
