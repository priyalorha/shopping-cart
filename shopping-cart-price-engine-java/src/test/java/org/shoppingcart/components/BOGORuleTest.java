package org.shoppingcart.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BOGORuleTest {

    private BOGORule bogoRule;

    @BeforeEach
    void setUp() {
        bogoRule = new BOGORule();
    }

    // ========== VALID INPUT TESTS ==========

    @ParameterizedTest(name = "[{index}] {1} items @ ${0} → ${2} (BOGO)")
    @CsvSource({
            // Even quantities
            "1.00, 2, 1.00",   // 2 items: pay for 1
            "2.50, 4, 5.00",    // 4 items: pay for 2
            "5.00, 10, 25.00",  // 10 items: pay for 5

            // Odd quantities
            "1.00, 1, 1.00",    // 1 item: pay for 1
            "3.00, 3, 6.00",    // 3 items: pay for 2 (1 pair + 1 single)
            "2.50, 5, 7.50",    // 5 items: pay for 3 (2 pairs + 1 single)

            // Edge cases
            "0.50, 0, 0.00",    // 0 items: pay 0
            "100.00, 1, 100.00", // Single expensive item
            "0.01, 999, 5.00"    // Large quantity with small unit price
    })
    @DisplayName("Should calculate correct BOGO price for valid inputs")
    void calculatePrice_validInputs_returnsCorrectPrice(double unitPrice, int quantity, double expectedPrice) {
        double actualPrice = bogoRule.calculatePrice(unitPrice, quantity);
        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    // ========== ERROR CONDITION TESTS ==========

    @ParameterizedTest
    @ValueSource(ints = {-1, -10, Integer.MIN_VALUE})
    @DisplayName("Should throw IllegalArgumentException for negative quantities")
    void calculatePrice_negativeQuantity_throwsException(int negativeQuantity) {
        assertThrows(IllegalArgumentException.class,
                () -> bogoRule.calculatePrice(1.00, negativeQuantity));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.01, -100.00, Double.MIN_VALUE})
    @DisplayName("Should throw IllegalArgumentException for negative unit prices")
    void calculatePrice_negativeUnitPrice_throwsException(double negativeUnitPrice) {
        assertThrows(IllegalArgumentException.class,
                () -> bogoRule.calculatePrice(-7, 2));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for NaN unit price")
    void calculatePrice_nanUnitPrice_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bogoRule.calculatePrice(Double.NaN, 1));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for infinite unit price")
    void calculatePrice_infiniteUnitPrice_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> bogoRule.calculatePrice(Double.POSITIVE_INFINITY, 1));
    }

    // ========== PRECISION TESTS ==========

    @ParameterizedTest
    @MethodSource("precisionTestCases")
    @DisplayName("Should handle decimal precision correctly")
    void calculatePrice_decimalValues_maintainsPrecision(double unitPrice, int quantity, double expectedPrice) {
        double actualPrice = bogoRule.calculatePrice(unitPrice, quantity);
        assertEquals(expectedPrice, actualPrice, 0.000001);
    }

    private static Stream<Arguments> precisionTestCases() {
        return Stream.of(
                Arguments.of(0.333333, 3, 0.666666),
                Arguments.of(0.10, 7, 0.40),
                Arguments.of(0.01, 1001, 5.01)
        );
    }

    // ========== LARGE NUMBER TESTS ==========

    @Test
    @DisplayName("Should handle maximum integer quantity")
    void calculatePrice_maxIntegerQuantity_returnsCorrectPrice() {
        double unitPrice = 1.00;
        double expectedPrice = (Integer.MAX_VALUE / 2 + Integer.MAX_VALUE % 2) * unitPrice;

        double actualPrice = bogoRule.calculatePrice(unitPrice, Integer.MAX_VALUE);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    @DisplayName("Should handle maximum double unit price")
    void calculatePrice_maxDoubleUnitPrice_returnsCorrectPrice() {
        int quantity = 2; // Even number to get simple case
        double expectedPrice = Double.MAX_VALUE;

        double actualPrice = bogoRule.calculatePrice(Double.MAX_VALUE, quantity);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    // ========== SPECIAL CASE TESTS ==========

    @Test
    @DisplayName("Should return zero for zero quantity regardless of unit price")
    void calculatePrice_zeroQuantity_returnsZero() {
        assertEquals(0.00, bogoRule.calculatePrice(100.00, 0), 0.001);
        assertEquals(0.00, bogoRule.calculatePrice(0.00, 0), 0.001);
        assertEquals(0.00, bogoRule.calculatePrice(-0.00, 0), 0.001);
    }

    @Test
    @DisplayName("Should handle zero unit price with non-zero quantity")
    void calculatePrice_zeroUnitPrice_returnsZero() {
        assertEquals(0.00, bogoRule.calculatePrice(0.00, 10), 0.001);
        assertEquals(0.00, bogoRule.calculatePrice(-0.00, 5), 0.001);
    }

    // ========== BEHAVIORAL TESTS ==========

    @Test
    @DisplayName("Should return same unit price for single item")
    void calculatePrice_singleItem_returnsUnitPrice() {
        double unitPrice = 1.99;
        assertEquals(unitPrice, bogoRule.calculatePrice(unitPrice, 1), 0.001);
    }

    @Test
    @DisplayName("Should return exactly half price for even quantities")
    void calculatePrice_evenQuantity_returnsHalfPrice() {
        double unitPrice = 2.00;
        int quantity = 4;
        double expectedPrice = (quantity / 2) * unitPrice;

        assertEquals(expectedPrice, bogoRule.calculatePrice(unitPrice, quantity), 0.001);
    }
}
