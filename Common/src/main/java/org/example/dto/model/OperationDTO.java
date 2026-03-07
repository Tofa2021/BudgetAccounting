package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Setter
@Getter
public abstract class OperationDTO implements Serializable {
    private Long id;
    private Integer amount;
    private Long userId;
}
