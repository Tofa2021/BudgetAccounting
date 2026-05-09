package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.CategoryService;
import org.example.domain.model.Category;
import org.example.dto.model.CategoryDTO;
import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

import java.util.List;

@RequiredArgsConstructor
public class CategoryRequestHandler {
    private final DTOMapper dtoMapper;
    private final CategoryService categoryService;

    public Response handle(Request request, Long userId) {
        return switch (request.getAction()) {
            case CREATE_CATEGORY -> {
                Long householdId = request.getParam("householdId");
                String name = request.getParam("name");
                String type = request.getParam("type");

                Category category = categoryService.create(householdId, name, type);
                yield Response.created(dtoMapper.toDTO(category, CategoryDTO.class));
            }

            case GET_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getAll(householdId);
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

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.getAction());
        };
    }
}
