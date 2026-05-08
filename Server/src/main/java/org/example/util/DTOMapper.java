package org.example.util;

import org.example.domain.model.BaseModel;
import org.example.dto.model.DTO;

import java.util.Collections;
import java.util.List;

public interface DTOMapper {
    <T extends BaseModel, R extends DTO> R toDTO(T model, Class<R> dtoClass);

    default <T extends BaseModel, R extends DTO> List<R> toDTOs(List<T> elements, Class<R> dtoClass) {
        if (elements == null) {
            return Collections.emptyList();
        }

        return elements
                .stream()
                .map(t -> toDTO(t, dtoClass))
                .toList();
    }

    <T extends DTO, R extends BaseModel> R fromDTO(T dto, Class<R> modelClass);
}
