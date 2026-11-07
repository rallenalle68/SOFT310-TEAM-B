package org.example;

public class ShippingService {

    /**
     * Calculates shipping fees based on zone, weight, and subtotal thresholds.
     */
    public double calculateShippingCost(Order order, double discountedSubtotal) {
        String zone = order.getShippingZone();

        // Free shipping boundary rule
        if (discountedSubtotal >= 150.0 && "DOMESTIC".equalsIgnoreCase(zone)) {
            return 0.0 + order.getExpressFee();
        }

        double baseShipping;
        switch (zone.toUpperCase()) {
            case "DOMESTIC":
                baseShipping = 10.0;
                break;
            case "REGIONAL":
                baseShipping = 25.0;
                break;
            case "INTERNATIONAL":
                baseShipping = 50.0;
                break;
            default:
                throw new IllegalArgumentException("Unknown shipping zone: " + zone);
        }

        // Order total adjustments
        if (discountedSubtotal < 50.0) {
            baseShipping += 5.0; // Surcharge for small orders
        }

        return baseShipping + order.getExpressFee();
    }
}