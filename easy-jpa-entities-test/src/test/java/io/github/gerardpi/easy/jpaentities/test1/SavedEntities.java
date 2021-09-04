package io.github.gerardpi.easy.jpaentities.test1;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

/**
 * This is a class to keep track of entity IDs in tests.
 */
public class SavedEntities {
    private final SortedMap<Integer, UUID> savedPersons = new TreeMap<>();
    private final SortedMap<Integer, UUID> savedAddresses = new TreeMap<>();
    private final SortedMap<Integer, UUID> savedPersonAddresses = new TreeMap<>();
    private final SortedMap<Integer, UUID> savedItems = new TreeMap<>();
    private final SortedMap<Integer, UUID> savedItemOrders = new TreeMap<>();
    private final SortedMap<Integer, UUID> savedItemOrderLines = new TreeMap<>();

    public void putPersonId(final int key, final UUID id) {
        this.savedPersons.put(key, id);
    }

    public void putAddressId(final int key, final UUID id) {
        this.savedAddresses.put(key, id);
    }

    public void putPersonAddressId(final int key, final UUID id) {
        this.savedPersonAddresses.put(key, id);
    }

    public void putItemId(final int key, final UUID id) {
        this.savedItems.put(key, id);
    }

    public void putItemOrderId(final int key, final UUID id) {
        this.savedItemOrders.put(key, id);
    }

    public void putItemOrderLineId(final int key, final UUID id) {
        this.savedItemOrderLines.put(key, id);
    }

    public UUID getPersonId(final int key) {
        return savedPersons.get(key);
    }

    public UUID getAddressId(final int key) {
        return savedAddresses.get(key);
    }

    public UUID getPersonAddressId(final int key) {
        return savedPersonAddresses.get(key);
    }

    public UUID getItemId(final int key) {
        return savedItems.get(key);
    }

    public UUID getItemOrderId(final int key) {
        return savedItemOrders.get(key);
    }

    public UUID getItemOrderLineId(final int key) {
        return savedItemOrderLines.get(key);
    }
}
