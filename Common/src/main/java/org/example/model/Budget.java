package org.example.model;

import lombok.Getter;

@Getter
public class Budget {
    private int amount = 0;

    public void plus(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to add cannot be negative: " + amount);
        }

        this.amount += amount;
        System.out.println("New budget amount: " + this.amount);
    }

    public void minus(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount to subtract cannot be negative: " + amount);
        }

        int newAmount = this.amount - amount;
        if (newAmount < 0) {
            throw new IllegalArgumentException("Final amount cannot be negative");
        }

        this.amount = newAmount;
        System.out.println("New budget amount: " + this.amount);
    }
}
