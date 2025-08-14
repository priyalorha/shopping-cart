package org.shoppingcart.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FruitType {
    APPLE(Constants.APPLE),
    BANANA(Constants.BANANA),
    LIME(Constants.LIME),
    MELON(Constants.MELON);

    private final String name;

    FruitType(String name) {
        this.name = name;
    }

    // This annotation tells Jackson to use this method when serializing
    @JsonValue
    public String getName() {
        return name;
    }

    // This annotation tells Jackson to use this method when deserializing
    @JsonCreator
    public static FruitType fromName(String name) {
        for (FruitType fruitType : values()) {
            if (fruitType.name.equalsIgnoreCase(name)) {
                return fruitType;
            }
        }
        throw new IllegalArgumentException("Unknown fruit type: " + name);
    }
}