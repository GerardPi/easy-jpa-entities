package io.github.gerardpi.easy.jpaentities.test1;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import io.github.gerardpi.easy.jpaentities.test1.domain.Currency;
import io.github.gerardpi.easy.jpaentities.test1.domain.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.TreeMap;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles(SpringProfile.TEST)
@SpringBootTest
public class PersistenceTests extends SimpleScenarioTest<PersistenceTests.State> {
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

    @Test
    public void the_database_contains_some_currencies() {
        when().a_currency_with_code_$_and_name_$_is_stored_in_the_database("EUR", "Euro");
        then().that_currency_$_can_be_fetched_from_the_database_using_the_currency_code_$("Euro", "EUR");
    }

    @Test
    public void the_optimistic_locking_value_is_increased_with_each_update_and_the_is_modified_flag_work_as_expected() {
        given().an_item_$_with_name_is_stored_in_the_database$(1, "kaas");
        when().fetching_that_item_$_from_the_database(1);
        then().that_item_$_modified("is not")
                .and().the_name_of_the_item_is_$("kaas")
                .and().the_optimistic_locking_version_value_of_the_item_is_$(0);
        when().creating_a_builder_for_modification()
                .and().the_name_in_the_builder_for_the_item_is_changed_into_$("fromage")
                .and().building_an_Item_from_the_builder();
        then().the_built_item_indicates_that_it_$_modified("is");
        when().the_newly_built_item_$_is_stored_into_the_database(2)
                .and().fetching_that_item_$_from_the_database(2);
        then().that_item_$_modified("is not")
                .and().the_optimistic_locking_version_value_of_the_item_is_$(1)
                .and().the_name_of_the_item_is_$("fromage");
    }

    static class State extends Stage<State> {
        private final SavedEntities savedEntities = new SavedEntities();
        private Repositories repositories;
        private UuidGenerator uuidGenerator;
        private Item fetchedItem;
        private Item.Builder itemBuilder;
        private Item rebuiltItem;

        @Hidden
        void init(UuidGenerator uuidGenerator, Repositories repositories) {
            this.uuidGenerator = uuidGenerator;
            this.repositories = repositories;
            repositories.clear();
        }


        State an_item_$_with_name_is_stored_in_the_database$(int itemNumber, @Quoted String name) {
            Item item = Item.create(uuidGenerator.generate(), "CHS01")
                    .setName(name)
                    .setImageNames(new TreeSet<>())
                    .setAttributes(new TreeMap<>())
                    .setTexts(new TreeMap<>())
                    .build();
            this.savedEntities.putItemId(itemNumber, repositories.getItemRepository().save(item).getId());
            return self();
        }


        State a_currency_with_code_$_and_name_$_is_stored_in_the_database(@Quoted String currencyCode, @Quoted String currencyName) {
            repositories.getCurrencyRepository().save(
                    Currency.create(uuidGenerator.generate())
                            .setCode(currencyCode)
                            .setName(currencyName)
                            .build());
            return self();
        }

        State that_currency_$_can_be_fetched_from_the_database_using_the_currency_code_$(@Quoted String expectedCurrencyName, @Quoted String currencyCode) {
            Currency currency = repositories.getCurrencyRepository().findByCode(currencyCode).get();
            assertThat(currency.getName()).isEqualTo(expectedCurrencyName);
            return self();
        }

        State fetching_that_item_$_from_the_database(int i) {
            this.fetchedItem = repositories.getItemRepository().findById(savedEntities.getItemId(i)).get();
            return self();
        }

        State creating_a_builder_for_modification() {
            this.itemBuilder = fetchedItem.modify();
            return self();
        }

        State the_name_in_the_builder_for_the_item_is_changed_into_$(@Quoted String newName) {
            this.itemBuilder.setName(newName).build();
            return self();
        }

        State that_item_$_modified(String isOrIsNot) {
            assertThat(this.fetchedItem.isModified()).isEqualTo(booleanValue("is", "is not", isOrIsNot));
            return self();
        }

        State the_optimistic_locking_version_value_of_the_item_is_$(int expectedOptLockVersion) {
            assertThat(this.fetchedItem.getEtag()).isEqualTo(expectedOptLockVersion);
            return self();

        }

        State the_built_item_indicates_that_it_$_modified(String isOrIsNot) {
            assertThat(this.rebuiltItem.isModified()).isEqualTo(booleanValue("is", "is not", isOrIsNot));
            return self();
        }

        boolean booleanValue(String trueValue, String falseValue, String value) {
            if (trueValue.equals(value)) {
                return true;
            }
            if (falseValue.equals(value)) {
                return false;
            }
            throw new IllegalArgumentException("Invalid value '" + value + "'. Must be either '" + trueValue + "' or '" + falseValue + "'");
        }

        State building_an_Item_from_the_builder() {
            this.rebuiltItem = itemBuilder.build();
            return self();
        }

        State the_newly_built_item_$_is_stored_into_the_database(int itemKey) {
            savedEntities.putItemId(itemKey, repositories.getItemRepository().save(rebuiltItem).getId());
            return self();
        }

        State the_name_of_the_item_is_$(@Quoted String expectedItemName) {
            assertThat(this.fetchedItem.getName()).isEqualTo(expectedItemName);
            return self();
        }
    }

}
