package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserDTO implements DTO, Serializable {
    private Long id;
    private String username;
    private List<Long> householdMemberIds;
}
