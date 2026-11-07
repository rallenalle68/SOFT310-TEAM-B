package org.example;

import java.util.HashMap;
import java.util.Map;

public class Order {
    private final String orderId;
    private final Customer customer;
    private final Map<Item, Integer> items = new HashMap<>();
    private String shippingZone; // "DOMESTIC", "REGIONAL", "INTERNATIONAL"
    private double ExpressFee = 0.0;
    private boolean processed = false;

    public Order(String orderId, Customer customer, String shippingZone) {
        this.orderId = orderId;
        this.customer = customer;
        this.shippingZone = shippingZone;
    }

    public void addItem(Item item, int quantity) {
        if (quantity <= 0 || quantity > 50) {
            throw new IllegalArgumentException("Item quantity must be between 1 and 50");
        }
        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public String getOrderId() { return orderId; }
    public Customer getCustomer() { return customer; }
    public Map<Item, Integer> getItems() { return items; }
    public String getShippingZone() { return shippingZone; }
    public double getExpressFee() { return ExpressFee; }
    public void setExpressFee(double expressFee) { ExpressFee = expressFee; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
