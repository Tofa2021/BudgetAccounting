package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
@AllArgsConstructor
public class HouseholdDTO implements DTO, Serializable {
    private Long id;
    private String name;
    private List<Long> memberIds;
    private List<Long> accountIds;
    private List<Long> categoryIds;
}
