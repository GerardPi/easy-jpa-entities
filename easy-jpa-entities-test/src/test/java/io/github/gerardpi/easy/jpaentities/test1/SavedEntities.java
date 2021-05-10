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

    void putPersonId(int key, UUID id) {
        this.savedPersons.put(key, id);
    }
    void putAddressId(int key, UUID id) {
        this.savedAddresses.put(key, id);
    }
    void putPersonAddressId(int key, UUID id) {
        this.savedPersonAddresses.put(key, id);
    }
    void putItemId(int key, UUID id) {
        this.savedItems.put(key, id);
    }
    void putItemOrderId(int key, UUID id) {
        this.savedItemOrders.put(key, id);
    }
    void putItemOrderLineId(int key, UUID id) {
        this.savedItemOrderLines.put(key, id);
    }
    UUID getPersonId(int key) {
        return savedPersons.get(key);
    }
    UUID getAddressId(int key) {
        return savedAddresses.get(key);
    }
    UUID getPersonAddressId(int key) {
        return savedPersonAddresses.get(key);
    }
    UUID getItemId(int key) {
        return savedItems.get(key);
    }
    UUID getItemOrderId(int key) {
        return savedItemOrders.get(key);
    }
    UUID getItemOrderLineId(int key) {
        return savedItemOrderLines.get(key);
    }
}
