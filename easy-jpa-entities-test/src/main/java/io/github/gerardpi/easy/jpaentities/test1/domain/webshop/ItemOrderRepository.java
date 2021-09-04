package io.github.gerardpi.easy.jpaentities.test1.domain.webshop;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ItemOrderRepository extends PagingAndSortingRepository<ItemOrder, UUID> {
    List<ItemOrder> findByPersonId(UUID personId);
}
