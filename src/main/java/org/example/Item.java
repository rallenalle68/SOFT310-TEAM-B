package org.example;

public class Item {
    private final String sku;
    private final String category; // "ELECTRONICS", "CLOTHING", "GROCERY"
    private final double unitPrice;
    private final boolean clearance;

    public Item(String sku, String category, double unitPrice, boolean clearance) {
        if (unitPrice <= 0.0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }
        this.sku = sku;
        this.category = category;
        this.unitPrice = unitPrice;
        this.clearance = clearance;
    }

    public String getSku() { return sku; }
    public String getCategory() { return category; }
    public double getUnitPrice() { return unitPrice; }
    public boolean isClearance() { return clearance; }
}
