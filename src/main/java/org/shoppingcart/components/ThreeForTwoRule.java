package org.shoppingcart.components;

import org.springframework.stereotype.Component;

@Component
public class ThreeForTwoRule implements PricingRule{
    @Override
    public double calculatePrice(double unitPrice, int quantity) {
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
        return ( (double) (quantity / 3) * 2  + quantity % 3) * unitPrice;
    }
}
