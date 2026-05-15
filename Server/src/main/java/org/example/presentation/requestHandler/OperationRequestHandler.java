package org.example.presentation.requestHandler;

import org.example.application.service.OperationService;
import org.example.domain.model.Operation;
import org.example.domain.model.OperationType;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OperationRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final OperationService operationService;

    public OperationRequestHandler(DTOMapper dtoMapper, OperationService operationService) {
        super(
                RequestAction.CREATE_OPERATION,
                RequestAction.GET_FILTERED_OPERATIONS,
                RequestAction.GET_HOUSEHOLD_OPERATIONS,
                RequestAction.GET_ACCOUNT_OPERATIONS,
                RequestAction.GET_MY_OPERATIONS,
                RequestAction.UPDATE_OPERATION,
                RequestAction.DELETE_OPERATION
        );
        this.dtoMapper = dtoMapper;
        this.operationService = operationService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_OPERATION -> {
                Long accountId = request.getParam("accountId");
                Long categoryId = request.getParam("categoryId");
                String description = request.getParam("description");
                BigDecimal amount = request.getParam("amount");
                Instant dateTime = request.getParam("dateTime");

                Operation operation = operationService.create(accountId, categoryId, description, amount, dateTime, userId);
                yield Response.created(dtoMapper.toOperationDTO(operation));
            }

            case GET_FILTERED_OPERATIONS -> {
                Long householdId = request.getParam("householdId");
                Long accountId = request.getParam("accountId");
                Long creatorUserId = request.getParam("creatorUserId");
                BigDecimal minAmount = request.getParam("minAmount");
                BigDecimal maxAmount = request.getParam("maxAmount");
                Instant dateFrom = request.getParam("dateFrom");
                Instant dateTo = request.getParam("dateTo");
                Long categoryId = request.getParam("categoryId");
                OperationType operationType = request.getParam("operationType");
                Integer limit = request.getParam("limit");
                String sortBy = request.getParam("sortBy");
                String sortDirection = request.getParam("sortDirection");

                List<Operation> operations = operationService.getFilteredOperations(
                        householdId,
                        accountId,
                        creatorUserId,
                        minAmount,
                        maxAmount,
                        dateFrom,
                        dateTo,
                        categoryId,
                        operationType,
                        limit,
                        sortBy,
                        sortDirection,
                        userId
                );
                yield Response.success(dtoMapper.toOperationDTOs(operations));
            }

            case GET_ACCOUNT_OPERATIONS -> {
                Long accountId = request.getParam("accountId");

                List<Operation> operations = operationService.getAccountOperations(accountId, userId);
                yield Response.success(dtoMapper.toOperationDTOs(operations));
            }

            case GET_HOUSEHOLD_OPERATIONS -> {
                Long householdId = request.getParam("householdId");

                List<Operation> operations = operationService.getHouseholdOperations(householdId, userId);
                yield Response.success(dtoMapper.toOperationDTOs(operations));
            }

            case GET_MY_OPERATIONS -> {
                Long householdId = request.getParam("householdId");

                List<Operation> operations = operationService.getMyOperations(householdId, userId);
                yield Response.success(dtoMapper.toOperationDTOs(operations));
            }

            case UPDATE_OPERATION -> {
                Long id = request.getParam("id");
                BigDecimal amount = request.getParam("amount");
                Long categoryId = request.getParam("categoryId");
                String description = request.getParam("description");
                Instant dateTime = request.getParam("dateTime");

                operationService.update(id, amount, categoryId, description, dateTime, userId);
                yield Response.noContent();
            }

            case DELETE_OPERATION -> {
                Long id = request.getParam("id");

                operationService.delete(id, userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
