package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.example.dto.OperationCategory;

@Entity
@DiscriminatorValue("MINUS")
@NoArgsConstructor
public class MinusBudgetOperation extends BudgetOperation {
    public MinusBudgetOperation(Integer amount, OperationCategory category) {
        super(amount, category);
    }
}
