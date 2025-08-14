package org.shoppingcart.services;

import org.shoppingcart.components.BOGORule;
import org.shoppingcart.components.NoDiscountRule;
import org.shoppingcart.components.ThreeForTwoRule;
import org.shoppingcart.models.*;
import org.shoppingcart.utils.FruitLoader;
import org.shoppingcart.utils.OfferType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class FruitBillService {

    private final BOGORule bogoRule;
    private final ThreeForTwoRule threeForTwoRule;
    private final NoDiscountRule noDiscountRule;
    private final FruitLoader fruitLoader;

    @Autowired
    public FruitBillService(BOGORule bogoRule,
                            ThreeForTwoRule threeForTwoRule,
                            NoDiscountRule noDiscountRule,
                            FruitLoader fruitLoader) {
        this.bogoRule = bogoRule;
        this.threeForTwoRule = threeForTwoRule;
        this.noDiscountRule = noDiscountRule;
        this.fruitLoader = fruitLoader;
    }

    public FruitResponseDTO billCalculator(List<FruitDTO> fruitDTOList) throws IOException {
        // Validate input
        if (fruitDTOList == null || fruitDTOList.isEmpty()) {
            return createEmptyResponse();
        }

        List<FruitPriceDTO> fruitPriceDTOList = new ArrayList<>();
        Map<String, Fruit> fruitMap = fruitLoader.loadFruitConfig();
        double cumulativeSum = 0.0;
        int cumulativeQuantity = 0;

        for (FruitDTO fruitDTO : fruitDTOList) {
            FruitPriceDTO pricedFruit = processFruitItem(fruitDTO, fruitMap);
            if (pricedFruit != null) {
                fruitPriceDTOList.add(pricedFruit);
                cumulativeSum += pricedFruit.getCharged();
                cumulativeQuantity += pricedFruit.getQuantity();
            }
        }

        return buildResponse(fruitPriceDTOList, cumulativeQuantity, cumulativeSum);
    }

    public FruitPriceDTO processFruitItem(FruitDTO fruitDTO, Map<String, Fruit> fruitMap) {
        Fruit fruit = fruitMap.get(fruitDTO.getFruit().toString());
        if (fruit == null) {
            return null; // or create a priced fruit with default values
        }

        FruitPriceDTO pricedFruit = new FruitPriceDTO();
        pricedFruit.setFruit(fruit.getName());
        pricedFruit.setQuantity(fruitDTO.getQuantity());
        pricedFruit.setUnitPrice(fruitMap.get(fruitDTO.getFruit().toString()).getPrice());
        pricedFruit.setOffer(fruit.getOfferType());
        double chargedAmount = calculateChargedAmount(pricedFruit);
        pricedFruit.setCharged(chargedAmount);
        pricedFruit.setAvgPrice(pricedFruit.getCharged()/pricedFruit.getUnitPrice());


        return pricedFruit;
    }

    private OfferType determineOfferType(String offerType) {
        try {
            return OfferType.valueOf(offerType);
        } catch (IllegalArgumentException e) {
            return OfferType.NoOffer;
        }
    }

    public double calculateChargedAmount(FruitPriceDTO pricedFruit) {
        return switch (pricedFruit.getOffer()) {
            case BOGO -> bogoRule.calculatePrice(
                    pricedFruit.getUnitPrice(),
                    pricedFruit.getQuantity());
            case ThreeForTwo -> threeForTwoRule.calculatePrice(
                    pricedFruit.getUnitPrice(),
                    pricedFruit.getQuantity());
            case NoOffer -> noDiscountRule.calculatePrice(
                    pricedFruit.getUnitPrice(),
                    pricedFruit.getQuantity());
        };
    }

    private FruitResponseDTO buildResponse(List<FruitPriceDTO> fruitPrices,
                                           int totalQuantity,
                                           double totalPrice) {
        FruitResponseDTO response = new FruitResponseDTO();
        response.setFruits(fruitPrices);
        response.setTotalQuantity(totalQuantity);
        response.setTotalPrice(totalPrice);
        return response;
    }

    private FruitResponseDTO createEmptyResponse() {
        return new FruitResponseDTO(Collections.emptyList(), 0, 0.0);
    }
}