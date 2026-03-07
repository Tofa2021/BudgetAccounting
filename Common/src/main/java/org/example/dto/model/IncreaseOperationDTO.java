package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.IncreaseOperationCategory;

@Setter
@Getter
public class IncreaseOperationDTO extends OperationDTO {
    private IncreaseOperationCategory category;

    public IncreaseOperationDTO(Long id, Integer amount, Long userId, IncreaseOperationCategory category) {
        super(id, amount, userId);
        this.category = category;
    }
}
