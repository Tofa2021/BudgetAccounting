package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.IncreaseOperationCategory;

@Entity
@DiscriminatorValue("PLUS")
@Getter
@Setter
@NoArgsConstructor
public class IncreaseBudgetOperation extends BudgetOperation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncreaseOperationCategory category;

    public IncreaseBudgetOperation(Integer amount, IncreaseOperationCategory category) {
        super(amount);
        this.category = category;
    }
}
