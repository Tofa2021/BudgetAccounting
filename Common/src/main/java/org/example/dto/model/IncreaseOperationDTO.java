package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.IncreaseOperationCategory;

import java.time.Instant;

@Setter
@Getter
public class IncreaseOperationDTO extends OperationDTO {
    private IncreaseOperationCategory category;

    public IncreaseOperationDTO(Long id, Integer amount, Instant dateTime, Long userId, IncreaseOperationCategory category) {
        super(id, amount, dateTime, userId);
        this.category = category;
    }
}
