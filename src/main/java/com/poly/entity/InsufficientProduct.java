package com.poly.entity;

public class InsufficientProduct {
    private String productName;
    private int insufficientQuantity;

    public InsufficientProduct(String productName, int insufficientQuantity) {
        this.productName = productName;
        this.insufficientQuantity = insufficientQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public int getInsufficientQuantity() {
        return insufficientQuantity;
    }
}

