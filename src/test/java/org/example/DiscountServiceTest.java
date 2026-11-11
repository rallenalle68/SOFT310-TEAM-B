package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DiscountService.calculateDiscountPercentage (Issue #30).
 * Covers each discount rule on its own, the boundaries between rules,
 * combined discounts, and the 40% global cap.
 */
class DiscountServiceTest {

    private static final double DELTA = 1e-9;

    private final DiscountService discountService = new DiscountService();

    // Baseline customer gets no discount: age 30, REGULAR, 0 points, not a student.
    private Customer customer(int age, CustomerTier tier, int points, boolean student) {
        return new Customer("C001", age, tier, points, student);
    }

    private Customer baseCustomer() {
        return customer(30, CustomerTier.REGULAR, 0, false);
    }

    // Order with a single non-clearance item, quantity 1.
    private Order orderFor(Customer customer) {
        Order order = new Order("O001", customer, "DOMESTIC");
        order.addItem(new Item("SKU1", "ELECTRONICS", 10.0, false), 1);
        return order;
    }

    private double discount(Customer customer, double subtotal) {
        return discountService.calculateDiscountPercentage(orderFor(customer), subtotal);
    }

    // ---------- Baseline ----------

    @Test
    void testNoDiscountForBaselineCustomer() {
        assertEquals(0.0, discount(baseCustomer(), 100.0), DELTA);
    }

    // ---------- 1. Loyalty points ----------

    @Test
    void testLoyalty99PointsNoDiscount() {
        assertEquals(0.0, discount(customer(30, CustomerTier.REGULAR, 99, false), 100.0), DELTA);
    }

    @Test
    void testLoyalty100PointsFivePercent() {
        assertEquals(0.05, discount(customer(30, CustomerTier.REGULAR, 100, false), 100.0), DELTA);
    }

    @Test
    void testLoyalty499PointsFivePercent() {
        assertEquals(0.05, discount(customer(30, CustomerTier.REGULAR, 499, false), 100.0), DELTA);
    }

    @Test
    void testLoyalty500PointsTenPercent() {
        assertEquals(0.10, discount(customer(30, CustomerTier.REGULAR, 500, false), 100.0), DELTA);
    }

    @Test
    void testLoyalty999PointsTenPercent() {
        assertEquals(0.10, discount(customer(30, CustomerTier.REGULAR, 999, false), 100.0), DELTA);
    }

    @Test
    void testLoyalty1000PointsFifteenPercent() {
        assertEquals(0.15, discount(customer(30, CustomerTier.REGULAR, 1000, false), 100.0), DELTA);
    }

    // ---------- Tier boost ----------

    @Test
    void testRegularTierNoBoost() {
        assertEquals(0.0, discount(customer(30, CustomerTier.REGULAR, 0, false), 100.0), DELTA);
    }

    @Test
    void testSilverTierNoBoost() {
        assertEquals(0.0, discount(customer(30, CustomerTier.SILVER, 0, false), 100.0), DELTA);
    }

    @Test
    void testGoldTierFivePercentBoost() {
        assertEquals(0.05, discount(customer(30, CustomerTier.GOLD, 0, false), 100.0), DELTA);
    }

    @Test
    void testPlatinumTierTenPercentBoost() {
        assertEquals(0.10, discount(customer(30, CustomerTier.PLATINUM, 0, false), 100.0), DELTA);
    }

    // ---------- 2. Age-based discounts ----------

    @Test
    void testAge64NoSeniorDiscount() {
        assertEquals(0.0, discount(customer(64, CustomerTier.REGULAR, 0, false), 100.0), DELTA);
    }

    @Test
    void testAge65SeniorDiscount() {
        assertEquals(0.08, discount(customer(65, CustomerTier.REGULAR, 0, false), 100.0), DELTA);
    }

    @Test
    void testSeniorStudentGetsOnlySeniorDiscount() {
        assertEquals(0.08, discount(customer(70, CustomerTier.REGULAR, 0, true), 100.0), DELTA);
    }

    @Test
    void testAge17StudentYouthDiscount() {
        assertEquals(0.05, discount(customer(17, CustomerTier.REGULAR, 0, true), 100.0), DELTA);
    }

    @Test
    void testAge17NonStudentNoYouthDiscount() {
        assertEquals(0.0, discount(customer(17, CustomerTier.REGULAR, 0, false), 100.0), DELTA);
    }

    @Test
    void testAge18StudentNoYouthDiscount() {
        assertEquals(0.0, discount(customer(18, CustomerTier.REGULAR, 0, true), 100.0), DELTA);
    }

    // ---------- 3. Subtotal thresholds ----------

    @Test
    void testSubtotal199_99NoDiscount() {
        assertEquals(0.0, discount(baseCustomer(), 199.99), DELTA);
    }

    @Test
    void testSubtotal200ThreePercent() {
        assertEquals(0.03, discount(baseCustomer(), 200.0), DELTA);
    }

    @Test
    void testSubtotal499_99ThreePercent() {
        assertEquals(0.03, discount(baseCustomer(), 499.99), DELTA);
    }

    @Test
    void testSubtotal500SevenPercent() {
        assertEquals(0.07, discount(baseCustomer(), 500.0), DELTA);
    }

    // ---------- 4. Bulk quantity ----------

    @Test
    void testBulk19ItemsNoDiscount() {
        Order order = new Order("O002", baseCustomer(), "DOMESTIC");
        order.addItem(new Item("SKU1", "CLOTHING", 5.0, false), 19);
        assertEquals(0.0, discountService.calculateDiscountPercentage(order, 95.0), DELTA);
    }

    @Test
    void testBulk20ItemsFivePercent() {
        Order order = new Order("O003", baseCustomer(), "DOMESTIC");
        order.addItem(new Item("SKU1", "CLOTHING", 5.0, false), 20);
        assertEquals(0.05, discountService.calculateDiscountPercentage(order, 100.0), DELTA);
    }

    @Test
    void testBulkCountsAcrossMultipleItems() {
        Order order = new Order("O004", baseCustomer(), "DOMESTIC");
        order.addItem(new Item("SKU1", "CLOTHING", 5.0, false), 12);
        order.addItem(new Item("SKU2", "GROCERY", 2.0, false), 8);
        assertEquals(0.05, discountService.calculateDiscountPercentage(order, 76.0), DELTA);
    }

    @Test
    void testClearanceItemsDoNotCountTowardBulk() {
        Order order = new Order("O005", baseCustomer(), "DOMESTIC");
        order.addItem(new Item("SKU1", "CLOTHING", 5.0, true), 20);
        assertEquals(0.0, discountService.calculateDiscountPercentage(order, 100.0), DELTA);
    }

    @Test
    void testMixedClearanceBelowBulkThreshold() {
        Order order = new Order("O006", baseCustomer(), "DOMESTIC");
        order.addItem(new Item("SKU1", "CLOTHING", 5.0, false), 10);
        order.addItem(new Item("SKU2", "CLOTHING", 5.0, true), 10);
        assertEquals(0.0, discountService.calculateDiscountPercentage(order, 100.0), DELTA);
    }

    // ---------- Combined discounts and the 40% cap ----------

    @Test
    void testCombinedDiscountsBelowCap() {
        // GOLD (5%) + 500 points (10%) + senior (8%) = 23%
        assertEquals(0.23, discount(customer(70, CustomerTier.GOLD, 500, false), 100.0), DELTA);
    }

    @Test
    void testCombinedDiscountExactlyAtCap() {
        // 1000 points (15%) + PLATINUM (10%) + senior (8%) + subtotal 500 (7%) = 40%
        assertEquals(0.40, discount(customer(70, CustomerTier.PLATINUM, 1000, false), 500.0), DELTA);
    }

    @Test
    void testDiscountCappedAt40Percent() {
        // 15% + 10% + 8% + 7% + bulk 5% = 45%, capped to 40%
        Customer customer = customer(70, CustomerTier.PLATINUM, 1000, false);
        Order order = new Order("O007", customer, "DOMESTIC");
        order.addItem(new Item("SKU1", "ELECTRONICS", 25.0, false), 20);
        assertEquals(0.40, discountService.calculateDiscountPercentage(order, 500.0), DELTA);
    }
}
