package org.shoppingcart.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shoppingcart.utils.FruitType;
import org.shoppingcart.utils.OfferType;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Fruit {
    @JsonProperty("name")
    private FruitType name;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("type")
    private OfferType offerType = OfferType.NoOffer;  // Matches JSON field "type"
    @JsonProperty("offer")
    private OfferDetails offerDetails; // Polymorphic field

    @Override
    public String toString() {
        return String.format(
                "{\"name\": \"%s\", \"price\": %.2f, \"offerType\": \"%s\", \"offerDetails\": %s}",
                name,
                price,
                offerType,
                offerDetails != null ? offerDetails.toString() : "null"
        );
    }
}
