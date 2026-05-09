package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class CategoryDTO implements DTO, Serializable {
    private Long id;
    private String name;
    private String type;
    private Long householdId;
}
