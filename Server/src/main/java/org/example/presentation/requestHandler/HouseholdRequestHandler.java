package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.HouseholdService;
import org.example.domain.model.Household;
import org.example.dto.HouseholdDTO;
import org.example.request.Request;
import org.example.response.Response;
import org.example.util.DTOMapper;

import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
public class HouseholdRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdService householdService;

    public Response handle(Request request, Long userId) {
        return switch (request.action()) {
            case CREATE_HOUSEHOLD -> {
                String name = request.getParam("name");
                Map<Long, String> startMembers = request.getParam("startMembers");

                Household household = householdService.create(name, startMembers, userId);
                yield Response.created(dtoMapper.toDTO(household, HouseholdDTO.class));
            }

            case GET_HOUSEHOLD -> {
                Long id = request.getParam("id");

                Household household = householdService.get(id);
                yield Response.success(dtoMapper.toDTO(household, HouseholdDTO.class));
            }

            case GET_HOUSEHOLD_AMOUNT -> {
                Long id = request.getParam("id");

                BigDecimal amount = householdService.getAmount(id);
                yield Response.success(amount);
            }

            case UPDATE_HOUSEHOLD -> {
                Long id = request.getParam("id");
                String name = request.getParam("name");

                householdService.update(id, name);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD -> {
                Long id = request.getParam("id");

                householdService.delete(id);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
