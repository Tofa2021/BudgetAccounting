package org.example.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "account_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountMember implements BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "household_member_id", nullable = false)
    private HouseholdMember householdMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountMemberRole role;

    @OneToMany(mappedBy = "accountMember", fetch = FetchType.LAZY)
    private List<Operation> operations;
}
