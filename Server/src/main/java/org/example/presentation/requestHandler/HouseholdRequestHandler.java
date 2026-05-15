package org.example.presentation.requestHandler;

import org.example.application.service.HouseholdService;
import org.example.domain.model.Household;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

import java.math.BigDecimal;
import java.util.Map;

public class HouseholdRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdService householdService;

    public HouseholdRequestHandler(DTOMapper dtoMapper, HouseholdService householdService) {
        super(
                RequestAction.CREATE_HOUSEHOLD,
                RequestAction.GET_HOUSEHOLD,
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                RequestAction.UPDATE_HOUSEHOLD,
                RequestAction.DELETE_HOUSEHOLD
        );
        this.dtoMapper = dtoMapper;
        this.householdService = householdService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_HOUSEHOLD -> {
                String name = request.getParam("name");
                Map<Long, String> startMembers = request.getParam("startMembers");

                Household household = householdService.create(name, startMembers, userId);
                yield Response.created(dtoMapper.toHouseholdDTO(household));
            }

            case GET_HOUSEHOLD -> {
                Long id = request.getParam("id");

                Household household = householdService.get(id, userId);
                yield Response.success(dtoMapper.toHouseholdDTO(household));
            }

            case GET_HOUSEHOLD_AMOUNT -> {
                Long id = request.getParam("id");

                BigDecimal amount = householdService.getAmount(id, userId);
                yield Response.success(amount);
            }

            case UPDATE_HOUSEHOLD -> {
                Long id = request.getParam("id");
                String name = request.getParam("name");

                householdService.update(id, name, userId);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD -> {
                Long id = request.getParam("id");

                householdService.delete(id, userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
