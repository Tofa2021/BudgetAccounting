package org.example.presentation.requestHandler;

import org.example.application.service.CategoryService;
import org.example.domain.model.Category;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

import java.util.List;

public class CategoryRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final CategoryService categoryService;

    public CategoryRequestHandler(DTOMapper dtoMapper, CategoryService categoryService) {
        super(
                RequestAction.CREATE_CATEGORY,
                RequestAction.GET_CATEGORIES,
                RequestAction.GET_INCOME_CATEGORIES,
                RequestAction.GET_EXPENSE_CATEGORIES,
                RequestAction.UPDATE_CATEGORY,
                RequestAction.DELETE_CATEGORY
        );
        this.dtoMapper = dtoMapper;
        this.categoryService = categoryService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_CATEGORY -> {
                Long householdId = request.getParam("householdId");
                String name = request.getParam("name");
                String type = request.getParam("type");

                Category category = categoryService.create(householdId, name, type, userId);
                yield Response.created(dtoMapper.toCategoryDTO(category));
            }

            case GET_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getAllByHouseholdId(householdId, userId);
                yield Response.success(dtoMapper.toCategoryDTOs(categories));
            }

            case GET_INCOME_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getIncomeCategories(householdId, userId);
                yield Response.success(dtoMapper.toCategoryDTOs(categories));
            }

            case GET_EXPENSE_CATEGORIES -> {
                Long householdId = request.getParam("householdId");

                List<Category> categories = categoryService.getExpenseCategories(householdId, userId);
                yield Response.success(dtoMapper.toCategoryDTOs(categories));
            }

            case UPDATE_CATEGORY -> {
                Long id = request.getParam("id");
                String name = request.getParam("name");
                String type = request.getParam("type");

                categoryService.update(id, name, type, userId);
                yield Response.noContent();
            }

            case DELETE_CATEGORY -> {
                Long id = request.getParam("id");

                categoryService.delete(id, userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
