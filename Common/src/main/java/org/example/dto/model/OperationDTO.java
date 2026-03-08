package org.example.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@AllArgsConstructor
@Setter
@Getter
public abstract class OperationDTO implements Serializable {
    private Long id;
    private Integer amount;
    private Instant dateTime;
    private Long userId;
}
