package org.shoppingcart.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FruitResponseDTO {

    List<FruitPriceDTO> fruits;
    Integer totalQuantity;
    Double totalPrice;

}
