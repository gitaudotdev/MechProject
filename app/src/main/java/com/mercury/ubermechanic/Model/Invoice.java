package com.mercury.ubermechanic.Model;

public class Invoice {
    private String amount;
    private String description;

    public Invoice() {
    }

    public Invoice(String amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
