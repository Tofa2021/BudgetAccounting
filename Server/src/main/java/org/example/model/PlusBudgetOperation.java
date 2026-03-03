package org.example.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import org.example.dto.OperationCategory;

@Entity
@DiscriminatorValue("PLUS")
@NoArgsConstructor
public class PlusBudgetOperation extends BudgetOperation {
    public PlusBudgetOperation(Integer amount, OperationCategory category) {
        super(amount, category);
    }
}
