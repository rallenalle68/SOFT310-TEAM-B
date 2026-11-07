package org.example;

import java.util.Map;

public class DiscountService {

    /**
     * Calculates total discount percentage (0.0 to 0.40 max allowed cap).
     * Ideal for Equivalence Partitioning & Boundary Value Analysis testing.
     */
    public double calculateDiscountPercentage(Order order, double subtotal) {
        Customer customer = order.getCustomer();
        double discount = 0.0;

        // 1. Loyalty Points / Tier Discount Rules (BVA Target)
        if (customer.getLoyaltyPoints() >= 1000) {
            discount += 0.15;
        } else if (customer.getLoyaltyPoints() >= 500) {
            discount += 0.10;
        } else if (customer.getLoyaltyPoints() >= 100) {
            discount += 0.05;
        }

        // Tier Boost
        if (customer.getTier() == CustomerTier.PLATINUM) {
            discount += 0.10;
        } else if (customer.getTier() == CustomerTier.GOLD) {
            discount += 0.05;
        }

        // 2. Age-Based / Special Discounts (EP & BVA Target)
        if (customer.getAge() >= 65) { // Senior Discount
            discount += 0.08;
        } else if (customer.getAge() < 18 && customer.isStudentStatus()) { // Youth Student
            discount += 0.05;
        }

        // 3. Subtotal Threshold Discount (BVA Target)
        if (subtotal >= 500.0) {
            discount += 0.07;
        } else if (subtotal >= 200.0) {
            discount += 0.03;
        }

        // 4. Bulk Item Quantity Discount
        int totalItemCount = 0;
        for (Map.Entry<Item, Integer> entry : order.getItems().entrySet()) {
            if (!entry.getKey().isClearance()) {
                totalItemCount += entry.getValue();
            }
        }

        if (totalItemCount >= 20) {
            discount += 0.05;
        }

        // Global Max Discount Cap: 40%
        return Math.min(discount, 0.40);
    }
}
