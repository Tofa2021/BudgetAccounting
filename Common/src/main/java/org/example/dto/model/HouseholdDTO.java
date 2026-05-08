package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HouseholdDTO implements DTO {
    private Long id;
    private String name;
    private List<Long> memberIds;
    private List<Long> accountIds;
    private List<Long> categoryIds;
}
