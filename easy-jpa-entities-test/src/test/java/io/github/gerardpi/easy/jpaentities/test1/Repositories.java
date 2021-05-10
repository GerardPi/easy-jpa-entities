package io.github.gerardpi.easy.jpaentities.test1;

import java.util.Optional;
import java.util.UUID;

public class Repositories {
    private final ItemRepository itemRepository;
    private final ItemOrderRepository itemOrderRepository;
    private final ItemOrderLineRepository itemOrderLineRepository;
    private final AddressRepository addressRepository;
    private final PersonRepository personRepository;
    private final PersonAddressRepository personAddressRepository;

    public Repositories(
            PersonRepository personRepository,
            AddressRepository addressRepository,
            PersonAddressRepository personAddressRepository,
            ItemRepository itemRepository,
            ItemOrderRepository itemOrderRepository,
            ItemOrderLineRepository itemOrderLineRepository
    ) {
        this.itemRepository = itemRepository;
        this.itemOrderRepository = itemOrderRepository;
        this.itemOrderLineRepository = itemOrderLineRepository;
        this.addressRepository = addressRepository;
        this.personRepository = personRepository;
        this.personAddressRepository = personAddressRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public ItemOrderRepository getItemOrderRepository() {
        return itemOrderRepository;
    }

    public ItemOrderLineRepository getItemOrderLineRepository() {
        return itemOrderLineRepository;
    }

    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    public PersonRepository getPersonRepository() {
        return personRepository;
    }

    public PersonAddressRepository getPersonAddressRepository() {
        return personAddressRepository;
    }
    public void clear() {
        this.itemOrderLineRepository.deleteAll();
        this.itemOrderRepository.deleteAll();
        this.itemRepository.deleteAll();

        this.personAddressRepository.deleteAll();
        this.addressRepository.deleteAll();
        this.personRepository.deleteAll();
    }

    @SuppressWarnings("unchecked")
    public <T extends OptLockablePersistable> Optional<T> fetchEntity(Class<T> entityClass, UUID id) {
        switch (entityClass.getSimpleName()) {
            case "Item":
                return (Optional<T>) itemRepository.findById(id);
            case "ItemOrder":
                return (Optional<T>) itemOrderRepository.findById(id);
            case "Person":
                return (Optional<T>) itemOrderLineRepository.findById(id);
            case "Address":
                return (Optional<T>) addressRepository.findById(id);
            case "PersonAddress":
                return (Optional<T>) personAddressRepository.findById(id);
            default:
                throw new IllegalStateException("Don't know entity class '" + entityClass.getName() + "'");
        }
    }

    public <T extends OptLockablePersistable> OptLockablePersistable getEntity(Class<T> entityClass, UUID id) {
        return fetchEntity(entityClass, id).orElseThrow(() -> new IllegalArgumentException("Could not find entity " + entityClass.getName() + " with id '" + id + "'"));
    }
}
