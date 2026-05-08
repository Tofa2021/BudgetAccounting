package org.example.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "household_members")
@Getter
@Setter
@NoArgsConstructor
public class HouseholdMember implements BaseModel, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "householdMember")
    private List<AccountMember> accountMembers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HouseholdMemberRole role;
}
