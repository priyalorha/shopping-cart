package org.shoppingcart.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.shoppingcart.utils.FruitType;
import org.shoppingcart.utils.OfferType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FruitDTO {

    @NotNull(message = "Fruit type is required")
    @JsonProperty("name")
    private FruitType fruit;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    @JsonProperty("quantity")
    private Integer quantity;
}
