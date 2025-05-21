package org.study;

import java.time.LocalDate;

class Transaction {
    private TransactionType type;
    private String category;
    private double amount;
    private String description;
    private LocalDate date;

    public Transaction(TransactionType type, String category, double amount, String description, LocalDate date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}