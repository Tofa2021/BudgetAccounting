package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

@Entity
@DiscriminatorValue("PLUS")
@Getter
@Setter
@NoArgsConstructor
public class IncreaseOperation extends Operation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncreaseOperationCategory category;

    public IncreaseOperation(Integer amount, IncreaseOperationCategory category) {
        super(amount);
        this.category = category;
    }

    @Override
    public OperationDTO toDTO() {
        return new IncreaseOperationDTO(getAmount(), getUser().getId(), category);
    }
}
