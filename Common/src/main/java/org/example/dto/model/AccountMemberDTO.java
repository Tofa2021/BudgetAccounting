package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountMemberDTO implements DTO {
    private Long id;
    private String role;
    private Long householdMemberId;
    private Long accountId;
}
