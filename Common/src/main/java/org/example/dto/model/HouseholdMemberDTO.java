package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class HouseholdMemberDTO implements DTO, Serializable {
    private Long id;
    private Long userId;
    private Long householdId;
    private List<Long> accountMemberIds;
    private String role;
}
