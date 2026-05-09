package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class AccountMemberDTO implements DTO, Serializable {
    private Long id;
    private String role;
    private Long householdMemberId;
    private Long accountId;
}
