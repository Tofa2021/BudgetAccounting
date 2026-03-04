package org.example.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.example.dto.DecreaseOperationCategory;

@Entity
@DiscriminatorValue("MINUS")
@NoArgsConstructor
public class DecreaseBudgetOperation extends BudgetOperation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecreaseOperationCategory category;

    public DecreaseBudgetOperation(Integer amount, DecreaseOperationCategory category) {
        super(amount);
        this.category = category;
    }
}
