package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.CategoryService;
import org.example.dto.model.CategoryDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.category.CreateCategoryRequest;
import org.example.dto.request.category.DeleteCategoryRequest;
import org.example.dto.request.category.GetCategoriesRequest;
import org.example.dto.request.category.UpdateCategoryRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class CategoryRequestHandler {
    private final DTOMapper dtoMapper;
    private final CategoryService categoryService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_CATEGORY ->
                    Response.created(dtoMapper.toDTO(categoryService.create((CreateCategoryRequest) request), CategoryDTO.class));

            case GET_CATEGORIES ->
                    Response.success(dtoMapper.toDTOs(categoryService.getAll((GetCategoriesRequest) request), CategoryDTO.class));

            case GET_INCOME_CATEGORIES ->
                    Response.success(dtoMapper.toDTOs(categoryService.getIncomeCategory((GetCategoriesRequest) request), CategoryDTO.class));

            case GET_EXPENSE_CATEGORIES ->
                    Response.success(dtoMapper.toDTOs(categoryService.getExpenseCategory((GetCategoriesRequest) request), CategoryDTO.class));

            case UPDATE_CATEGORY -> {
                categoryService.update((UpdateCategoryRequest) request);
                yield Response.noContent();
            }

            case DELETE_CATEGORY -> {
                categoryService.delete((DeleteCategoryRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
