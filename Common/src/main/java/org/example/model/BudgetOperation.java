package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public abstract class BudgetOperation implements Serializable {
    private int amount;
}
