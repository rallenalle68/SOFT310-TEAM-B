
# Boundary Value Analysis

## 1. Loyalty Points

| Test Value | Expected Result |
|---:|---|
| 99 | No loyalty discount |
| 100 | 5% loyalty discount |
| 101 | 5% loyalty discount |
| 499 | 5% loyalty discount |
| 500 | 10% loyalty discount |
| 501 | 10% loyalty discount |
| 999 | 10% loyalty discount |
| 1000 | 15% loyalty discount |
| 1001 | 15% loyalty discount |

## 2. Customer Age

| Test Value | Expected Result |
|---:|---|
| 17 | 5% youth student discount (if student) |
| 18 | No youth discount |
| 19 | No youth discount |
| 64 | No senior discount |
| 65 | 8% senior discount |
| 66 | 8% senior discount |

## 3. Order Subtotal

| Test Value | Expected Result |
|---:|---|
| 199.99 | No subtotal discount |
| 200.00 | 3% subtotal discount |
| 200.01 | 3% subtotal discount |
| 499.99 | 3% subtotal discount |
| 500.00 | 7% subtotal discount |
| 500.01 | 7% subtotal discount |

## 4. Bulk Purchase Quantity

| Test Value | Expected Result |
|---:|---|
| 19 | No bulk discount |
| 20 | 5% bulk discount |
| 21 | 5% bulk discount |

## 5. Shipping Subtotal

| Test Value | Expected Result |
|---:|---|
| 49.99 | $5 surcharge |
| 50.00 | No surcharge |
| 50.01 | No surcharge |
| 149.99 | Normal domestic shipping |
| 150.00 | Free domestic shipping |
| 150.01 | Free domestic shipping |

## 6. Item Quantity

| Test Value | Expected Result |
|---:|---|
| 0 | Invalid quantity |
| 1 | Valid quantity |
| 2 | Valid quantity |
| 49 | Valid quantity |
| 50 | Valid quantity |
| 51 | Invalid quantity |

## Conclusion

Boundary Value Analysis was used to identify values immediately
below, at, and immediately above each decision threshold.

The tests verify that the application behaves correctly at
these boundaries and rejects quantities outside the permitted range.
