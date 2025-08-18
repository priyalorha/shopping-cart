package org.shoppingcart.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BOGORule implements PricingRule {

    @Override
    public double calculatePrice(double unitPrice, int quantity) {
        // Input validation
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (Double.isNaN(unitPrice)) {
            throw new IllegalArgumentException("Unit price cannot be NaN");
        }
        if (Double.isInfinite(unitPrice)) {
            throw new IllegalArgumentException("Unit price cannot be infinite");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }

        // Core BOGO calculation logic
        return (quantity / 2 * 1 + quantity % 2) * unitPrice;
    }
}
