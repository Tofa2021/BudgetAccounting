package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "budgets")
@Getter
@Setter
@NoArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount = 0.;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public void increase(double amount) {
        this.amount = this.amount + amount;
    }

    public void decrease(double amount) {
        double result = this.amount - amount;
        if (result < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = result;
    }
}
