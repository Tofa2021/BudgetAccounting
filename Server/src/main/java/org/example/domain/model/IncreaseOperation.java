package org.example.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.time.Instant;

@Entity
@DiscriminatorValue("PLUS")
@Getter
@Setter
@NoArgsConstructor
public class IncreaseOperation extends Operation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncreaseOperationCategory category;

    public IncreaseOperation(Double amount, IncreaseOperationCategory category) {
        super(amount);
        this.category = category;
    }

    public IncreaseOperation(Long id, Double amount, Instant dateTime, User user, IncreaseOperationCategory category) {
        super(id, amount, dateTime, user);
        this.category = category;
    }

    @Override
    public OperationDTO toDTO() {
        return new IncreaseOperationDTO(getId(), getAmount(), getDateTime(), getUser().getId(), category);
    }
}
