package org.example.dto.model;

import java.util.List;

public class HouseholdMemberDTO implements DTO {
    private Long id;
    private Long userId;
    private Long householdId;
    private List<Long> accountMemberIds;
    private String role;
}
