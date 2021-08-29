package io.github.gerardpi.easy.jpaentities.test1.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyRepository extends PagingAndSortingRepository<Currency, UUID> {
    Optional<Currency> findByCode(String code);
}
