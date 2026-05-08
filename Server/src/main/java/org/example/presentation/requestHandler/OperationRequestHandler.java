package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.OperationService;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.IntegerAuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.operation.DeleteOperationRequest;
import org.example.dto.request.operation.OperationFilterRequest;
import org.example.dto.request.operation.OperationRequest;
import org.example.dto.request.operation.UpdateOperationRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class OperationRequestHandler {
    private final DTOMapper dtoMapper;
    private final OperationService operationService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_OPERATION ->
                    Response.created(dtoMapper.toDTO(operationService.create((OperationRequest) request, userId), OperationDTO.class));

            case GET_USER_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getAllByUserId(userId), OperationDTO.class));

            case GET_RECENT_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getRecentOperations((IntegerAuthorizedRequest) request, userId), OperationDTO.class));

            case GET_FILTERED_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getFilteredOperations((OperationFilterRequest) request, userId), OperationDTO.class));

            case UPDATE_OPERATION -> {
                operationService.update((UpdateOperationRequest) request);
                yield Response.noContent();
            }

            case DELETE_OPERATION -> {
                operationService.delete((DeleteOperationRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
