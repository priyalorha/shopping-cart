package org.shoppingcart.components;

import org.springframework.stereotype.Component;

@Component
public class NoDiscountRule implements PricingRule {
    @Override
    public double calculatePrice(double unitPrice, int quantity) {
        return unitPrice * quantity;
    }
}
