package org.example.dto.model;

import java.util.List;

public class UserDTO implements DTO {
    private Long id;
    private String username;
    private List<Long> householdMemberIds;
}
