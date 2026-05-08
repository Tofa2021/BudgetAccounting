package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO implements DTO {
    private Long id;
    private String name;
    private String type;
    private Long householdId;
}
