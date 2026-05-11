package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.CategoryService;
import org.example.domain.model.Category;
import org.example.dto.CategoryDTO;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.request.Request;
import org.example.response.Response;

import java.util.List;

@RequiredArgsConstructor
public class CategoryRequestHandler {
    private final DTOMapper dtoMapper;
    private final CategoryService categoryService;

    public Response handle(Request request, Long userId) {
        return switch (request.action()) {
            case CREATE_CATEGORY -> {
                Long householdId = request.getParam("householdId");
                String name = request.getParam("name");
                String type = request.getParam("type");

                Category category = categoryService.create(householdId, name, type);
                yield Response.created(dtoMapper.toDTO(category, CategoryDTO.class));
            }

            case GET_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getAllByHouseholdId(householdId);
                yield Response.success(dtoMapper.toDTOs(categories, CategoryDTO.class));
            }

            case GET_INCOME_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getIncomeCategory(householdId);
                yield Response.success(dtoMapper.toDTOs(categories, CategoryDTO.class));
            }

            case GET_EXPENSE_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getExpenseCategory(householdId);
                yield Response.success(dtoMapper.toDTOs(categories, CategoryDTO.class));
            }

            case UPDATE_CATEGORY -> {
                Long id = request.getParam("id");
                String name = request.getParam("name");
                String type = request.getParam("type");

                categoryService.update(id, name, type);
                yield Response.noContent();
            }

            case DELETE_CATEGORY -> {
                Long id = request.getParam("id");

                categoryService.delete(id);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
