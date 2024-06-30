package com.jamesaldteves.test2;

public class TransactionModel {
    private String amount;
    private String category;
    private String date;
    private String note;

    // Default no-arg constructor is required for Firebase data to object conversion
    public TransactionModel() {
    }

    // Constructor with all parameters
    public TransactionModel(String amount, String category, String date, String note) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // Getters
    public String getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    // Setters
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNote(String note) {
        this.note = note;
    }

    // toString method to represent the TransactionModel object in string format if needed
    @Override
    public String toString() {
        return "TransactionModel{" +
                "amount='" + amount + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
