package org.example.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category implements BaseModel, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationType type;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Operation> operations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id") // TODO think if we need system category if we dont need it add nullable = false
    private Household household;
}
