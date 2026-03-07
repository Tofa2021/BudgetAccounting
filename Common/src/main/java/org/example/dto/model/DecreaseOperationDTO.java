package org.example.dto.model;

import lombok.Getter;
import org.example.dto.DecreaseOperationCategory;

@Getter
public class DecreaseOperationDTO extends OperationDTO {
    private final DecreaseOperationCategory category;

    public DecreaseOperationDTO(Integer amount, Long userId, DecreaseOperationCategory category) {
        super(amount, userId);
        this.category = category;
    }
}
