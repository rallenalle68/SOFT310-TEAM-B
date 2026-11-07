package org.example;

import java.util.Map;

public class OrderProcessor {
    private final DiscountService discountService;
    private final ShippingService shippingService;

    public OrderProcessor(DiscountService discountService, ShippingService shippingService) {
        this.discountService = discountService;
        this.shippingService = shippingService;
    }

    /**
     * Prime target for Data-Flow Testing (DEF-USE tracking of subtotal, discount, tax, finalTotal)
     */
    public double processOrder(Order order) throws OrderProcessingException {
        if (order.isProcessed()) {
            throw new OrderProcessingException("Order has already been processed");
        }
        if (order.getItems().isEmpty()) {
            throw new OrderProcessingException("Cannot process an empty order");
        }

        // Step 1: Calculate raw subtotal (def: subtotal)
        double subtotal = 0.0;
        for (Map.Entry<Item, Integer> entry : order.getItems().entrySet()) {
            Item item = entry.getKey();
            int qty = entry.getValue();
            subtotal += item.getUnitPrice() * qty; // (use & re-def: subtotal)
        }

        // Step 2: Apply discount (use: subtotal; def: discountRate, discountedSubtotal)
        double discountRate = discountService.calculateDiscountPercentage(order, subtotal);
        double discountedSubtotal = subtotal * (1.0 - discountRate);

        // Step 3: Calculate shipping (use: discountedSubtotal; def: shippingCost)
        double shippingCost = shippingService.calculateShippingCost(order, discountedSubtotal);

        // Step 4: Calculate tax (use: discountedSubtotal; def: tax)
        // Grocery items are tax-exempt
        double taxableAmount = 0.0;
        for (Map.Entry<Item, Integer> entry : order.getItems().entrySet()) {
            Item item = entry.getKey();
            if (!"GROCERY".equalsIgnoreCase(item.getCategory())) {
                double itemTotal = item.getUnitPrice() * entry.getValue();
                taxableAmount += itemTotal * (1.0 - discountRate);
            }
        }

        double tax = taxableAmount * 0.08; // 8% sales tax

        // Step 5: Calculate final total (def: finalTotal)
        double finalTotal = discountedSubtotal + shippingCost + tax;

        // Step 6: Update customer loyalty points state
        Customer customer = order.getCustomer();
        int earnedPoints = (int) (finalTotal / 10.0);
        customer.setLoyaltyPoints(customer.getLoyaltyPoints() + earnedPoints);

        order.setProcessed(true);
        return finalTotal;
    }
}
