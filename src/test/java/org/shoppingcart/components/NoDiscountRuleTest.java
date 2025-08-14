package org.shoppingcart.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class NoDiscountRuleTest {

    private NoDiscountRule noDiscountRule;

    @BeforeEach
    void setUp() {
        noDiscountRule = new NoDiscountRule();
    }

    // ========== BASIC FUNCTIONALITY TESTS ==========

    @ParameterizedTest(name = "[{index}] {1} items @ ${0} → ${2}")
    @CsvSource({
            "1.00, 1, 1.00",
            "2.50, 2, 5.00",
            "5.00, 3, 15.00",
            "0.50, 10, 5.00",
            "100.00, 1, 100.00"
    })
    @DisplayName("Should calculate correct price for valid inputs")
    void calculatePrice_validInputs_returnsCorrectPrice(double unitPrice, int quantity, double expectedPrice) {
        double actualPrice = noDiscountRule.calculatePrice(unitPrice, quantity);
        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Should return zero for zero quantity")
    void calculatePrice_zeroQuantity_returnsZero() {
        assertEquals(0.00, noDiscountRule.calculatePrice(100.00, 0), 0.001);
    }

    @Test
    @DisplayName("Should return zero for zero unit price")
    void calculatePrice_zeroUnitPrice_returnsZero() {
        assertEquals(0.00, noDiscountRule.calculatePrice(0.00, 10), 0.001);
    }

    @Test
    @DisplayName("Should handle negative zero unit price")
    void calculatePrice_negativeZeroUnitPrice_returnsZero() {
        assertEquals(0.00, noDiscountRule.calculatePrice(-0.0, 5), 0.001);
    }

    // ========== LARGE NUMBER TESTS ==========

    @Test
    @DisplayName("Should handle maximum integer quantity")
    void calculatePrice_maxIntegerQuantity_returnsCorrectPrice() {
        double unitPrice = 1.00;
        double expectedPrice = unitPrice * Integer.MAX_VALUE;

        double actualPrice = noDiscountRule.calculatePrice(unitPrice, Integer.MAX_VALUE);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    @DisplayName("Should handle maximum double unit price")
    void calculatePrice_maxDoubleUnitPrice_returnsCorrectPrice() {
        int quantity = 2;
        double expectedPrice = Double.MAX_VALUE * quantity;

        double actualPrice = noDiscountRule.calculatePrice(Double.MAX_VALUE, quantity);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    // ========== SPECIAL VALUE TESTS ==========

    @Test
    @DisplayName("Should handle NaN unit price")
    void calculatePrice_nanUnitPrice_returnsNaN() {
        double result = noDiscountRule.calculatePrice(Double.NaN, 2);
        assertTrue(Double.isNaN(result));
    }

    @Test
    @DisplayName("Should handle positive infinity unit price")
    void calculatePrice_positiveInfinityUnitPrice_returnsInfinity() {
        double result = noDiscountRule.calculatePrice(Double.POSITIVE_INFINITY, 2);
        assertTrue(Double.isInfinite(result));
        assertEquals(Double.POSITIVE_INFINITY, result);
    }

    @Test
    @DisplayName("Should handle negative infinity unit price")
    void calculatePrice_negativeInfinityUnitPrice_returnsNegativeInfinity() {
        double result = noDiscountRule.calculatePrice(Double.NEGATIVE_INFINITY, 2);
        assertTrue(Double.isInfinite(result));
        assertEquals(Double.NEGATIVE_INFINITY, result);
    }

    // ========== PRECISION TESTS ==========

    @ParameterizedTest
    @ValueSource(doubles = {0.333333, 0.10, 0.01})
    @DisplayName("Should maintain decimal precision")
    void calculatePrice_decimalValues_maintainsPrecision(double unitPrice) {
        int quantity = 3;
        double expectedPrice = unitPrice * quantity;
        double actualPrice = noDiscountRule.calculatePrice(unitPrice, quantity);

        assertEquals(expectedPrice, actualPrice, 0.000001);
    }

    // ========== BEHAVIORAL TESTS ==========

    @Test
    @DisplayName("Should return same unit price for single item")
    void calculatePrice_singleItem_returnsUnitPrice() {
        double unitPrice = 1.99;
        assertEquals(unitPrice, noDiscountRule.calculatePrice(unitPrice, 1), 0.001);
    }

    @Test
    @DisplayName("Should return exact multiple for multiple items")
    void calculatePrice_multipleItems_returnsExactMultiple() {
        double unitPrice = 2.50;
        int quantity = 4;
        double expectedPrice = unitPrice * quantity;

        assertEquals(expectedPrice, noDiscountRule.calculatePrice(unitPrice, quantity), 0.001);
    }
}
