package org.shoppingcart.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.shoppingcart.components.*;
import org.shoppingcart.models.*;
import org.shoppingcart.utils.FruitLoader;
import org.shoppingcart.utils.FruitType;
import org.shoppingcart.utils.OfferType;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FruitBillServiceTest {

    @Mock
    private BOGORule bogoRule;

    @Mock
    private ThreeForTwoRule threeForTwoRule;

    @Mock
    private NoDiscountRule noDiscountRule;

    @Mock
    private FruitLoader fruitLoader;

    @InjectMocks
    private FruitBillService fruitBillService;

    private Map<String, Fruit> testFruitMap;

    @BeforeEach
    void setUp() {
        testFruitMap = new HashMap<>();

        OfferDetails offerDetails = new BOGO(1, 1);
        Fruit fruit = new Fruit();

        fruit.setName(FruitType.APPLE);
        fruit.setPrice(0.60);
        fruit.setOfferType(OfferType.NoOffer);

        testFruitMap.put("APPLE", fruit);

        fruit.setName(FruitType.BANANA);
        fruit.setPrice(0.20);
        fruit.setOfferType(OfferType.NoOffer);


        testFruitMap.put("BANANA", fruit);

        fruit.setName(FruitType.MELON);
        fruit.setPrice(0.20);
        fruit.setOfferType(OfferType.BOGO);
        fruit.setOfferDetails(offerDetails);

        testFruitMap.put("MELON", fruit);

        offerDetails = new ThreeForTwo(1, 1);

        fruit.setName(FruitType.LIME);
        fruit.setPrice(0.20);
        fruit.setOfferType(OfferType.ThreeForTwo);
        fruit.setOfferDetails(offerDetails);
        testFruitMap.put("LIME", fruit);
    }

    @Test
    void billCalculator_EmptyList_ReturnsEmptyResponse() throws IOException {
        // Act
        FruitResponseDTO response = fruitBillService.billCalculator(Collections.emptyList());

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalQuantity());
        assertEquals(0.0, response.getTotalPrice());
        assertTrue(response.getFruits().isEmpty());
    }

    @Test
    void billCalculator_NullList_ReturnsEmptyResponse() throws IOException {
        // Act
        FruitResponseDTO response = fruitBillService.billCalculator(null);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalQuantity());
        assertEquals(0.0, response.getTotalPrice());
        assertTrue(response.getFruits().isEmpty());
    }

    @Test
    void billCalculator_UnknownFruit_ReturnsNullForThatItem() throws IOException {
        // Arrange
        List<FruitDTO> input = List.of(new FruitDTO(FruitType.LIME, 2));


        // Act
        FruitResponseDTO response = fruitBillService.billCalculator(input);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalQuantity());
        assertEquals(0.0, response.getTotalPrice());
        assertTrue(response.getFruits().isEmpty());
    }

    @Test
    void billCalculator_IOException_ThrowsException() throws IOException {
        // Arrange
        List<FruitDTO> input = List.of(new FruitDTO(FruitType.APPLE, 1));
        when(fruitLoader.loadFruitConfig()).thenThrow(new IOException("File error"));

        // Act & Assert
        assertThrows(IOException.class, () -> fruitBillService.billCalculator(input));
    }


    @Test
    void calculateChargedAmount_NoOffer_AppliesNoDiscountRule() {
        // Arrange
        FruitPriceDTO pricedFruit = new FruitPriceDTO();
        pricedFruit.setUnitPrice(0.25);
        pricedFruit.setQuantity(2);
        pricedFruit.setOffer(OfferType.NoOffer);
        when(noDiscountRule.calculatePrice(0.25, 2)).thenReturn(0.50);

        // Act
        double result = fruitBillService.calculateChargedAmount(pricedFruit);

        // Assert
        assertEquals(0.50, result, 0.001);
    }
}
