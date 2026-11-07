package org.example;

public class Customer {
    private final String id;
    private final int age;
    private CustomerTier tier;
    private int loyaltyPoints;
    private boolean studentStatus;

    public Customer(String id, int age, CustomerTier tier, int loyaltyPoints, boolean studentStatus) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("Invalid age provided");
        }
        this.id = id;
        this.age = age;
        this.tier = tier;
        this.loyaltyPoints = Math.max(0, loyaltyPoints);
        this.studentStatus = studentStatus;
    }

    public String getId() { return id; }
    public int getAge() { return age; }
    public CustomerTier getTier() { return tier; }
    public void setTier(CustomerTier tier) { this.tier = tier; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public boolean isStudentStatus() { return studentStatus; }
}
