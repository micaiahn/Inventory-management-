package com.pims.model;

import java.math.BigDecimal;

/** Represents one line item in the cashier's current shopping cart. */
public class CartItem {
    private final int medicineId;
    private final String name;
    private final BigDecimal unitPrice;
    private int quantity;
    private final int availableStock;

    public CartItem(int medicineId, String name, BigDecimal unitPrice, int quantity, int availableStock) {
        this.medicineId = medicineId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.availableStock = availableStock;
    }

    public int getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getAvailableStock() { return availableStock; }

    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
