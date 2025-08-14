package org.shoppingcart.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OfferType {
    BOGO(Constants.BOGO),
    ThreeForTwo(Constants.THREE4TWO),
    NoOffer(Constants.NoOffer);

    private final String offerName;

    OfferType(String offerName) {
        this.offerName = offerName;
    }


    // Proper getter method without syntax errors
    @JsonValue  // Serializes using this value
    public String getOfferName() {
        return this.offerName;
    }

    // Safe parsing from String with default value
    @JsonCreator  // Used for deserialization
    public static OfferType fromString(String value) {
        if (value == null) {
            return NoOffer;
        }

        // Case-insensitive comparison
        String normalizedValue = value.trim().toUpperCase();

        for (OfferType type : values()) {
            if (type != NoOffer && normalizedValue.equals(type.name())) {
                return type;
            }
            if (type.offerName != null && type.offerName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return NoOffer;  // Default for unknown values
    }

}

