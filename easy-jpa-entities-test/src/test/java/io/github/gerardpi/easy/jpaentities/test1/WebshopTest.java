package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
public class WebshopTest extends SimpleScenarioTest<WebshopTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(WebshopTest.class);

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemOrderRepository itemOrderRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PersonAddressRepository personAddressRepository;
    @Autowired
    private UuidGenerator uuidGenerator;
    @ScenarioStage
    private State state;

    @BeforeEach
    public void init() {
        ((FixedUuidSeriesGenerator) uuidGenerator).reset();
        state.init(uuidGenerator, personAddressRepository, addressRepository, personRepository, itemRepository, itemOrderRepository);
    }

    @Disabled("Not implemented yet")
    @Test
    public void test() {
        given().a_person_$_with_first_name_$_and_last_name_$(1, "A", "B")
                .an_item_$_with_name_$(1, "kaas");
        when().an_order_$_by_$_of_$_of_item_$_for_$_$_per_item(1, 1, 2, 1, "EUR", new BigDecimal("10.00"));
    }


    static class State extends Stage<State> {
        private final SortedMap<Integer, UUID> savedPersons = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedAddresses = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedPersonAddresses = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedItemOrders = new TreeMap<>();
        private final SortedMap<Integer, UUID> savedItems = new TreeMap<>();
        private ItemRepository itemRepository;
        private ItemOrderRepository itemOrderRepository;
        private AddressRepository addressRepository;
        private PersonRepository personRepository;
        private PersonAddressRepository personAddressRepository;
        private UuidGenerator uuidGenerator;

        @Hidden
        void init(UuidGenerator uuidGenerator, PersonAddressRepository personAddressRepository, AddressRepository addressRepository, PersonRepository personRepository, ItemRepository itemRepository, ItemOrderRepository itemOrderRepository) {
            this.uuidGenerator = uuidGenerator;
            this.addressRepository = addressRepository;
            this.personRepository = personRepository;
            this.personAddressRepository = personAddressRepository;
            this.itemRepository = itemRepository;
            this.itemOrderRepository = itemOrderRepository;
            this.personAddressRepository.deleteAll();
            this.addressRepository.deleteAll();
            this.personRepository.deleteAll();
            this.itemOrderRepository.deleteAll();
            this.itemRepository.deleteAll();
        }

        State a_person_$_with_first_name_$_and_last_name_$(@Quoted int number, @Quoted String nameFirst, @Quoted String nameLast) {
            Person person = Person.create(uuidGenerator.generate())
                    .setDateOfBirth(LocalDate.now())
                    .setNameFirst(nameFirst)
                    .setNameLast(nameLast)
                    .build();
            this.savedPersons.put(number, personRepository.save(person).getId());
            return self();
        }

        State an_item_$_with_name_$(int itemNumber, @Quoted String name) {
            Item item = Item.create(uuidGenerator.generate())
                    .setName(name)
                    .setImageNames(new TreeSet<>())
                    .setAttributes(new TreeMap<>())
                    .setTexts(new TreeMap<>())
                    .build();
            this.savedItems.put(itemNumber, itemRepository.save(item).getId());
            return self();
        }

        State an_order_$_by_$_of_$_of_item_$_for_$_$_per_item(int itemOrderNumber, int personNumber, int itemCount, int itemNumber, String currencyCode, BigDecimal amountPerItem) {
            /*
            ItemOrder itemOrder = ItemOrder.create(uuidGenerator.generate())
                    .setPersonId(this.savedPersons.get(personNumber));
                    .addOrderLine(ItemOrderLine.create(uuidGenerator.generate())
                        .setItemId(savedItems.get(itemNumber))
                        .setAmountPerItem(amountPerItem)
                        .setCurrencyCode(currencyCode)
                        .setCount(itemCount))
                    .build();
            LOG.info("itemOrder={}", itemOrder);
            ItemOrder savedItemOrder = itemOrderRepository.save(itemOrder);
            this.savedItemOrders.put(itemOrderNumber, savedItemOrder.getId());
            LOG.info("savedItemOrder={}", savedItemOrder);

             */
            return self();
        }

        OptLockablePersistable getEntity(Class<?> entityClass, int number) {
            switch (entityClass.getSimpleName()) {
                case "Item":
                    return itemRepository.findById(savedItems.get(number)).get();
                case "ItemOrder":
                    return itemOrderRepository.findById(savedItemOrders.get(number)).get();
                case "Person":
                    return personRepository.findById(savedPersons.get(number)).get();
                case "Address":
                    return addressRepository.findById(savedAddresses.get(number)).get();
                case "PersonAddress":
                    return personAddressRepository.findById(savedPersonAddresses.get(number)).get();
                default:
                    throw new IllegalStateException("Don't know entity class '" + entityClass.getName() + "'");
            }
        }

    }

}
