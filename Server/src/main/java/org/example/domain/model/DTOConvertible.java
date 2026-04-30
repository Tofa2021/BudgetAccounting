package org.example.domain.model;

import org.example.dto.model.DTO;

public interface DTOConvertible<T extends DTO> {
    T toDTO();
}
