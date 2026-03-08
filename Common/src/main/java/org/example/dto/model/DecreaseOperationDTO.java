package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;

import java.time.Instant;

@Setter
@Getter
public class DecreaseOperationDTO extends OperationDTO {
    private DecreaseOperationCategory category;

    public DecreaseOperationDTO(Long id, Integer amount, Instant dateTime, Long userId, DecreaseOperationCategory category) {
        super(id, amount, dateTime, userId);
        this.category = category;
    }
}
