package org.shoppingcart.components;

public interface PricingRule {
    double calculatePrice(double unitPrice, int quantity);
}