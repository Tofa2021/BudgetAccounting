package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.OperationCategory;

import java.io.Serializable;

@Entity
@Table(name = "operations")
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "operation_type")
public abstract class BudgetOperation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationCategory category;

    public BudgetOperation(Integer amount, OperationCategory category) {
        this.amount = amount;
        this.category = category;
    }
}
