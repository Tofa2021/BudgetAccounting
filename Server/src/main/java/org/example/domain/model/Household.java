package org.example.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "households")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Household implements BaseModel, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "household", fetch = FetchType.LAZY)
    private List<HouseholdMember> members;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "household")
    private List<Account> accounts;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "household")
    private List<Category> categories;
}
