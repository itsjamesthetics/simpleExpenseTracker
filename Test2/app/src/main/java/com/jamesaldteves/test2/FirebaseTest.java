package com.jamesaldteves.test2;

public class FirebaseTest {
    private String category, budgetEnabled, dailyBudget, weeklyBudget, monthlyBudget, YearlyBudget, Note;

    public FirebaseTest() {}

    public FirebaseTest(String category, String budgetEnabled, String dailyBudget, String weeklyBudget, String monthlyBudget, String yearlyBudget, String note) {
        this.category = category;
        this.budgetEnabled = budgetEnabled;
        this.dailyBudget = dailyBudget;
        this.weeklyBudget = weeklyBudget;
        this.monthlyBudget = monthlyBudget;
        this.YearlyBudget = yearlyBudget;
        this.Note = note;
    }
}
