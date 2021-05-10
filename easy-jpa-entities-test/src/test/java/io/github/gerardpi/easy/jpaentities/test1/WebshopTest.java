package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;


@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
public class WebshopTest extends SimpleScenarioTest<WebshopTest.State> {
    private static final Logger LOG = LoggerFactory.getLogger(WebshopTest.class);

    @Autowired
    private Repositories repositories;
    @Autowired
    private UuidGenerator uuidGenerator;
    @ScenarioStage
    private State state;

    @BeforeEach
    public void init() {
        ((FixedUuidSeriesGenerator) uuidGenerator).reset();
        state.init(uuidGenerator, repositories);
    }

    //    @Disabled("Not implemented yet")
    @Test
    public void test() {
        given().a_person_$_with_first_name_$_and_last_name_$(1, "A", "B")
                .an_item_$_with_name_$(1, "kaas");
        when().an_order_$_is_stored_for_person_$(1, 1)
                .and()
                .that_order_$_contains_$_pieces_of_$_which_cost_$_$_a_piece(1, 1, 1, "EUR 10.00");
    }


    static class State extends Stage<State> {
        private final SavedEntities savedEntities = new SavedEntities();
        private Repositories repositories;
        private UuidGenerator uuidGenerator;
        private Supplier<OffsetDateTime> dateTimeSupplier;

        @Hidden
        void init(UuidGenerator uuidGenerator, Repositories repositories) {
            this.uuidGenerator = uuidGenerator;
            this.repositories = repositories;
            repositories.clear();
            this.dateTimeSupplier = OffsetDateTime::now;
        }

        State a_person_$_with_first_name_$_and_last_name_$(@Quoted int number, @Quoted String nameFirst, @Quoted String nameLast) {
            Person person = Person.create(uuidGenerator.generate())
                    .setDateOfBirth(LocalDate.now())
                    .setNameFirst(nameFirst)
                    .setNameLast(nameLast)
                    .build();
            this.savedEntities.putPersonId(number, repositories.getPersonRepository().save(person).getId());
            return self();
        }

        State an_item_$_with_name_$(int itemNumber, @Quoted String name) {
            Item item = Item.create(uuidGenerator.generate())
                    .setName(name)
                    .setImageNames(new TreeSet<>())
                    .setAttributes(new TreeMap<>())
                    .setTexts(new TreeMap<>())
                    .build();
            this.savedEntities.putItemId(itemNumber, repositories.getItemRepository().save(item).getId());
            return self();
        }

        State an_order_$_is_stored_for_person_$(int itemOrderNumber, int personNumber) {
            ItemOrder itemOrder = ItemOrder.create(uuidGenerator.generate())
                    .setPersonId(this.savedEntities.getPersonId(personNumber))
                    .setDateTime(dateTimeSupplier.get())
                    .build();
            this.savedEntities.putItemOrderId(itemOrderNumber, repositories.getItemOrderRepository().save(itemOrder).getId());
            return self();
        }

        State that_order_$_contains_$_pieces_of_$_which_cost_$_$_a_piece(int itemOrderNumber, int itemNumber, int itemCount, @Quoted String amountPerItemStr) {
            Money amountPerItem = Money.parse(amountPerItemStr);
            ItemOrderLine itemOrderLine = ItemOrderLine.create(uuidGenerator.generate())
                    .setItemOrderId(savedEntities.getItemOrderId(1))
                    .setItemId(savedEntities.getItemId(itemNumber))
                    .setAmountPerItem(amountPerItem.getNumberStripped())
                    .setCurrencyCode(amountPerItem.getCurrency().getCurrencyCode())
                    .setCount(itemCount)
                    .build();
            this.savedEntities.putItemOrderLineId(itemOrderNumber, repositories.getItemOrderLineRepository().save(itemOrderLine).getId());
            return self();
        }

    }

}
