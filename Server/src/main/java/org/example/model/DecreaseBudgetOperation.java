package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;

@Entity
@DiscriminatorValue("MINUS")
@Getter
@Setter
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
