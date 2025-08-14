package org.shoppingcart.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ThreeForTwoRuleTest {

    private ThreeForTwoRule threeForTwoRule;

    @BeforeEach
    void setUp() {
        threeForTwoRule = new ThreeForTwoRule();
    }

    // ===== CORE DISCOUNT LOGIC TESTS =====
    @ParameterizedTest(name = "UnitPrice ${0} x {1} items → ${2} (3-for-2)")
    @CsvSource({
            // Exact multiples of 3 (full discount sets)
            "1.00, 3, 2.00",   // 3 items = pay for 2
            "2.50, 6, 10.00",   // 6 items = pay for 4
            "5.00, 9, 30.00",  // 9 items = pay for 6

            // Not multiples of 3 (partial sets)
            "1.00, 1, 1.00",   // 1 item = no discount
            "3.00, 2, 6.00",   // 2 items = no discount
            "2.50, 4, 7.50",   // 4 items = 3+1 (pay for 2+1)
            "5.00, 5, 20.00",  // 5 items = 3+2 (pay for 2+2)
            "10.00, 7, 50.00", // 7 items = 6+1 (pay for 4+1)

            // Edge quantities
            "0.99, 0, 0.00",   // 0 items = free
            "100.00, 1, 100.00" // Single expensive item
    })
    void calculatePrice_standardCases_correctDiscount(double unitPrice, int quantity, double expected) {
        assertEquals(expected, threeForTwoRule.calculatePrice(unitPrice, quantity), 0.001);
    }

    // ===== PRECISION HANDLING =====
    @ParameterizedTest
    @CsvSource({
            "0.333, 3, 0.666",
            "0.10, 7, 0.50",
            "0.01, 100, 0.67"  // 100 items = 66.666 → rounded
    })
    void calculatePrice_decimalPrices_properRounding(double unitPrice, int quantity, double expected) {
        double actual = threeForTwoRule.calculatePrice(unitPrice, quantity);
        assertEquals(expected, actual, 0.01,
                () -> String.format("Expected %.3f but got %.3f", expected, actual));
    }

    // ===== ERROR CASES =====
    @ParameterizedTest
    @ValueSource(ints = {-1, -100, Integer.MIN_VALUE})
    void calculatePrice_negativeQuantity_throwsException(int invalidQty) {
        assertThrows(IllegalArgumentException.class,
                () -> threeForTwoRule.calculatePrice(1.00, invalidQty),
                "Should reject negative quantities");
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -100.00, Double.NEGATIVE_INFINITY})
    void calculatePrice_invalidUnitPrice_throwsException(double invalidPrice) {
        assertThrows(IllegalArgumentException.class,
                () -> threeForTwoRule.calculatePrice(invalidPrice, 1),
                "Should reject negative prices");
    }

    @Test
    void calculatePrice_nanPrice_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> threeForTwoRule.calculatePrice(Double.NaN, 1));
    }

    // ===== BOUNDARY CASES =====
    @Test
    void calculatePrice_maxIntegerQuantity_correctCalculation() {
        int maxQty = Integer.MAX_VALUE;
        double unitPrice = 1.00;
        int fullSets = maxQty / 3;
        int remainder = maxQty % 3;
        double expected = (fullSets * 2 + remainder) * unitPrice;

        assertEquals(expected, threeForTwoRule.calculatePrice(unitPrice, maxQty),
                "Should handle max integer quantity");
    }

    @Test
    void calculatePrice_largeUnitPrice_correctCalculation() {
        double largePrice = Double.MAX_VALUE;
        assertEquals(2 * largePrice,
                threeForTwoRule.calculatePrice(largePrice, 3),
                0.001,
                "Should handle max double price");
    }

    // ===== SPECIAL CASES =====
    @Test
    void calculatePrice_zeroQuantity_returnsZero() {
        assertEquals(0.00, threeForTwoRule.calculatePrice(999.99, 0));
    }

    @Test
    void calculatePrice_zeroUnitPrice_returnsZero() {
        assertEquals(0.00, threeForTwoRule.calculatePrice(0.00, 100));
    }
}