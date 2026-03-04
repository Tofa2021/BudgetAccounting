package org.example.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.example.dto.IncreaseBudgetCategory;

@Entity
@DiscriminatorValue("PLUS")
@NoArgsConstructor
public class IncreaseBudgetOperation extends BudgetOperation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncreaseBudgetCategory category;

    public IncreaseBudgetOperation(Integer amount, IncreaseBudgetCategory category) {
        super(amount);
        this.category = category;
    }
}
