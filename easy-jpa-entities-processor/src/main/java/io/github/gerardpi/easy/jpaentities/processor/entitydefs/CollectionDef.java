package io.github.gerardpi.easy.jpaentities.processor.entitydefs;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionDef {
    private final String collectionType;
    private final String collectedType;

    CollectionDef(String type) {
        List<String> types = getTypes(type);
        this.collectionType = types.get(0);
        this.collectedType = types.get(1);
    }

    public String getCollectionType() {
        return collectionType;
    }

    public String getCollectedType() {
        return collectedType;
    }

    private static final Pattern RE_TYPE = Pattern.compile("^([^<>]+)<([^<>]+)>$");

    public static List<String> getTypes(String type) {
        if (!isSupportedCollection(type)) {
            throw new IllegalStateException("Collection in '" + type + "' is not supported.");
        }
        Matcher matcher = RE_TYPE.matcher(type);
        if (matcher.matches()) {
            return Arrays.asList(matcher.group(1), matcher.group(2));
        }
        throw new IllegalStateException("Could not extract type in collection from '" + type + "'");
    }

    private static final Map<String, String> SUPPORTED_COLLECTIONS = createSupportedCollections();

    private static Map<String, String> createSupportedCollections() {
        Map<String, String> result = new HashMap<>();
        result.put(Set.class.getName(), HashSet.class.getName());
        result.put(List.class.getName(), ArrayList.class.getName());
        result.put(SortedSet.class.getName(), TreeSet.class.getName());
        return Collections.unmodifiableMap(result);
    }

    public boolean isSortedSet() {
        return SortedSet.class.getName().equals(collectionType);
    }
    public boolean isList() {
        return List.class.getName().equals(collectionType);
    }
    public boolean isSet() {
        return Set.class.getName().equals(collectionType);
    }

    public String getCollectionImplementationType() {
        return SUPPORTED_COLLECTIONS.get(collectionType);
    }

    public static boolean isSupportedCollection(String type) {
        return SUPPORTED_COLLECTIONS.keySet().stream()
                .anyMatch(type::startsWith);
    }
}
