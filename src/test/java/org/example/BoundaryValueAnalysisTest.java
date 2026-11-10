
package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundaryValueAnalysisTest {

    private final DiscountService discountService = new DiscountService();
    private final ShippingService shippingService = new ShippingService();

    private Order createOrder(int age, int loyaltyPoints, boolean student) {
        Customer customer = new Customer(
                "C001", age, CustomerTier.REGULAR, loyaltyPoints, student
        );

        return new Order("O001", customer, "DOMESTIC");
    }

    private double calculateDiscount(int age, int points, boolean student, double subtotal) {
        Order order = createOrder(age, points, student);
        return discountService.calculateDiscountPercentage(order, subtotal);
    }

    @Test
    void testLoyaltyPointsBoundaries() {
        assertAll(
                () -> assertEquals(0.0, calculateDiscount(30, 99, false, 100), 0.0001),
                () -> assertEquals(0.05, calculateDiscount(30, 100, false, 100), 0.0001),
                () -> assertEquals(0.05, calculateDiscount(30, 101, false, 100), 0.0001),
                () -> assertEquals(0.05, calculateDiscount(30, 499, false, 100), 0.0001),
                () -> assertEquals(0.10, calculateDiscount(30, 500, false, 100), 0.0001),
                () -> assertEquals(0.10, calculateDiscount(30, 501, false, 100), 0.0001),
                () -> assertEquals(0.10, calculateDiscount(30, 999, false, 100), 0.0001),
                () -> assertEquals(0.15, calculateDiscount(30, 1000, false, 100), 0.0001),
                () -> assertEquals(0.15, calculateDiscount(30, 1001, false, 100), 0.0001)
        );
    }

    @Test
    void testAgeBoundaries() {
        assertAll(
                () -> assertEquals(0.05, calculateDiscount(17, 0, true, 100), 0.0001),
                () -> assertEquals(0.0, calculateDiscount(18, 0, true, 100), 0.0001),
                () -> assertEquals(0.0, calculateDiscount(19, 0, true, 100), 0.0001),
                () -> assertEquals(0.0, calculateDiscount(64, 0, true, 100), 0.0001),
                () -> assertEquals(0.08, calculateDiscount(65, 0, true, 100), 0.0001),
                () -> assertEquals(0.08, calculateDiscount(66, 0, true, 100), 0.0001)
        );
    }

    @Test
    void testSubtotalBoundaries() {
        assertAll(
                () -> assertEquals(0.0, calculateDiscount(30, 0, false, 199.99), 0.0001),
                () -> assertEquals(0.03, calculateDiscount(30, 0, false, 200), 0.0001),
                () -> assertEquals(0.03, calculateDiscount(30, 0, false, 200.01), 0.0001),
                () -> assertEquals(0.03, calculateDiscount(30, 0, false, 499.99), 0.0001),
                () -> assertEquals(0.07, calculateDiscount(30, 0, false, 500), 0.0001),
                () -> assertEquals(0.07, calculateDiscount(30, 0, false, 500.01), 0.0001)
        );
    }

    @Test
    void testBulkQuantityBoundaries() {
        assertAll(
                () -> assertEquals(0.0, calculateBulkDiscount(19), 0.0001),
                () -> assertEquals(0.05, calculateBulkDiscount(20), 0.0001),
                () -> assertEquals(0.05, calculateBulkDiscount(21), 0.0001)
        );
    }

    private double calculateBulkDiscount(int quantity) {
        Order order = createOrder(30, 0, false);
        Item item = new Item("ITEM001", "ELECTRONICS", 10.0, false);
        order.addItem(item, quantity);

        return discountService.calculateDiscountPercentage(order, 100);
    }

    @Test
    void testShippingSubtotalBoundaries() {
        assertAll(
                () -> assertEquals(15.0, calculateShipping(49.99), 0.0001),
                () -> assertEquals(10.0, calculateShipping(50), 0.0001),
                () -> assertEquals(10.0, calculateShipping(50.01), 0.0001),
                () -> assertEquals(10.0, calculateShipping(149.99), 0.0001),
                () -> assertEquals(0.0, calculateShipping(150), 0.0001),
                () -> assertEquals(0.0, calculateShipping(150.01), 0.0001)
        );
    }

    private double calculateShipping(double subtotal) {
        Order order = createOrder(30, 0, false);
        return shippingService.calculateShippingCost(order, subtotal);
    }

    @Test
    void testMinimumQuantityBoundaries() {
        Order order = createOrder(30, 0, false);
        Item item = new Item("ITEM002", "CLOTHING", 20.0, false);

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(item, 0)
        );

        order.addItem(item, 1);
        assertEquals(1, order.getItems().get(item));
        
        order.addItem(item, 1);
        assertEquals(2, order.getItems().get(item));
    }

    @Test
    void testMaximumQuantityBoundaries() {
        Order order = createOrder(30, 0, false);
        Item item = new Item("ITEM003", "CLOTHING", 20.0, false);

        order.addItem(item, 49);
        assertEquals(49, order.getItems().get(item));

        order.addItem(item, 1);
        assertEquals(50, order.getItems().get(item));

        assertThrows(
                IllegalArgumentException.class,
                () -> order.addItem(item, 51)
        );
    }
}
