package org.example.dto.model;

import lombok.Getter;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;

@Setter
@Getter
public class DecreaseOperationDTO extends OperationDTO {
    private DecreaseOperationCategory category;

    public DecreaseOperationDTO(Long id, Integer amount, Long userId, DecreaseOperationCategory category) {
        super(id, amount, userId);
        this.category = category;
    }
}
