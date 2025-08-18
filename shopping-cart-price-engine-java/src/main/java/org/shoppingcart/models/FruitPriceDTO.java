package org.shoppingcart.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.shoppingcart.utils.FruitType;
import org.shoppingcart.utils.OfferType;

@Data

public class FruitPriceDTO {

    private FruitType fruit;
    private OfferType offer;
    private Double unitPrice;
    private Integer quantity;
    private Double charged;
    private Double avgPrice;

}
