package org.shoppingcart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = BOGO.class, name = "BOGO"),
        @JsonSubTypes.Type(value = ThreeForTwo.class, name = "ThreeForTwo")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class OfferDetails {
    // Common properties/methods can go here
}