package org.example.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.time.Instant;

@Entity
@DiscriminatorValue("MINUS")
@Getter
@Setter
@NoArgsConstructor
public class DecreaseOperation extends Operation {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecreaseOperationCategory category;

    public DecreaseOperation(Double amount, DecreaseOperationCategory category) {
        super(amount);
        this.category = category;
    }

    public DecreaseOperation(Long id, Double amount, Instant dateTime, User user, DecreaseOperationCategory category) {
        super(id, amount, dateTime, user);
        this.category = category;
    }

    @Override
    public OperationDTO toDTO() {
        return new DecreaseOperationDTO(getId(), getAmount(), getDateTime(), getUser().getId(), category);
    }
}
