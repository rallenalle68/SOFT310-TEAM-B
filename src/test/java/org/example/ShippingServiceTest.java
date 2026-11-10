
package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ShippingServiceTest {

    private final ShippingService shippingService = new ShippingService();

    private Order createOrder(String zone, double expressFee) {
        Customer customer = new Customer(
                "C001", 25, CustomerTier.REGULAR, 0, false
        );

        Order order = new Order("O001", customer, zone);
        order.setExpressFee(expressFee);
        return order;
    }

    @Test
    void testDomesticShipping() {
        Order order = createOrder("DOMESTIC", 0);
        assertEquals(10.0, shippingService.calculateShippingCost(order, 100));
    }

    @Test
    void testRegionalShipping() {
        Order order = createOrder("REGIONAL", 0);
        assertEquals(25.0, shippingService.calculateShippingCost(order, 100));
    }

    @Test
    void testInternationalShipping() {
        Order order = createOrder("INTERNATIONAL", 0);
        assertEquals(50.0, shippingService.calculateShippingCost(order, 100));
    }

    @Test
    void testFreeDomesticShippingAt150() {
        Order order = createOrder("DOMESTIC", 0);
        assertEquals(0.0, shippingService.calculateShippingCost(order, 150));
    }

    @Test
    void testDomesticShippingBelowFreeShippingThreshold() {
        Order order = createOrder("DOMESTIC", 0);
        assertEquals(10.0, shippingService.calculateShippingCost(order, 149.99));
    }

    @Test
    void testSurchargeBelow50() {
        Order order = createOrder("DOMESTIC", 0);
        assertEquals(15.0, shippingService.calculateShippingCost(order, 49.99));
    }

    @Test
    void testNoSurchargeAt50() {
        Order order = createOrder("DOMESTIC", 0);
        assertEquals(10.0, shippingService.calculateShippingCost(order, 50));
    }

    @Test
    void testSurchargeAppliesToRegionalShipping() {
        Order order = createOrder("REGIONAL", 0);
        assertEquals(30.0, shippingService.calculateShippingCost(order, 49.99));
    }

    @Test
    void testSurchargeAppliesToInternationalShipping() {
        Order order = createOrder("INTERNATIONAL", 0);
        assertEquals(55.0, shippingService.calculateShippingCost(order, 49.99));
    }

    @Test
    void testExpressShippingFeeIsAdded() {
        Order order = createOrder("REGIONAL", 5);
        assertEquals(30.0, shippingService.calculateShippingCost(order, 100));
    }

    @Test
    void testExpressFeeAddedToFreeShipping() {
        Order order = createOrder("DOMESTIC", 7.5);
        assertEquals(7.5, shippingService.calculateShippingCost(order, 150));
    }

    @Test
    void testUnknownShippingZoneThrowsException() {
        Order order = createOrder("UNKNOWN", 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> shippingService.calculateShippingCost(order, 100)
        );
    }

    @Test
    void testShippingZoneIsCaseInsensitive() {
        Order order = createOrder("domestic", 0);
        assertEquals(10.0, shippingService.calculateShippingCost(order, 100));
    }
}
