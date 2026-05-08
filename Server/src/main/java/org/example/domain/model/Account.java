package org.example.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account implements BaseModel, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<AccountMember> members;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private BigDecimal amount = BigDecimal.ZERO;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "account")
    private List<Operation> operations;

    @ManyToOne(fetch = FetchType.LAZY)
    private Household household;

    public void increase(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    public void decrease(BigDecimal amount) {
        BigDecimal result = this.amount.subtract(amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = result;
    }
}
