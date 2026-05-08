package org.example.util;

import org.example.domain.model.BaseModel;
import org.example.domain.model.Operation;
import org.example.dto.model.DTO;
import org.example.dto.model.OperationDTO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DTOMapperImpl implements DTOMapper {
    private final Map<Class<? extends BaseModel>, Function<BaseModel, DTO>> modelToDTOMap = new ConcurrentHashMap<>();

    public DTOMapperImpl() {
        modelToDTOMap.put(Operation.class, (model) -> createOperationDTO((Operation) model));
    }

    @Override
    public <T extends BaseModel, R extends DTO> R toDTO(T model, Class<R> dtoClass) {
        Function<BaseModel, DTO> mapper = modelToDTOMap.get(model.getClass());
        return (R) mapper.apply(model);
    }

    @Override
    public <T extends DTO, R extends BaseModel> R fromDTO(T dto, Class<R> modelClass) {
        return null;
    }

    private OperationDTO createOperationDTO(Operation operation) {
        if (operation == null) {
            return null;
        }

        return new OperationDTO(
                operation.getId(),
                operation.getBudget() != null ? operation.getBudget().getId() : null,
                operation.getUser() != null ? operation.getUser().getId() : null,
                operation.getCategory(),
                operation.getAmount(),
                operation.getDateTime()
        );
    }
}