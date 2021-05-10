package io.github.gerardpi.easy.jpaentities.test1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableTransactionManagement
@Transactional
public class TestConfig {
    private static final Logger LOG = LoggerFactory.getLogger(TestConfig.class);
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemOrderRepository itemOrderRepository;
    @Autowired
    private ItemOrderLineRepository itemOrderLineRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PersonAddressRepository personAddressRepository;

    @Bean
    Repositories repositories(
            PersonRepository personRepository,
            AddressRepository addressRepository,
            PersonAddressRepository personAddressRepository,
            ItemRepository itemRepository,
            ItemOrderRepository itemOrderRepository,
            ItemOrderLineRepository itemOrderLineRepository
    ) {
        return new Repositories(personRepository, addressRepository, personAddressRepository, itemRepository, itemOrderRepository, itemOrderLineRepository);
    }

    @Profile(SpringProfile.TEST)
    @Bean
    UuidGenerator uuidGenerator() {
        return new FixedUuidSeriesGenerator();
    }
}
