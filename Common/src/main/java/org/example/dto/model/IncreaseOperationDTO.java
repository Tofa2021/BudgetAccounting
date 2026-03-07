package org.example.dto.model;

import lombok.Getter;
import org.example.dto.IncreaseOperationCategory;

@Getter
public class IncreaseOperationDTO extends OperationDTO {
    private final IncreaseOperationCategory category;

    public IncreaseOperationDTO(Integer amount, Long userId, IncreaseOperationCategory category) {
        super(amount, userId);
        this.category = category;
    }
}
