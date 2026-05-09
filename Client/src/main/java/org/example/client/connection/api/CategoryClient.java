package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.CategoryDTO;
import org.example.dto.request.RequestAction;
import org.example.dto.request.category.CreateCategoryRequest;
import org.example.dto.request.category.DeleteCategoryRequest;
import org.example.dto.request.category.GetCategoriesRequest;
import org.example.dto.request.category.UpdateCategoryRequest;

public class CategoryClient extends BaseClient {
    public CategoryClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<CategoryDTO> create(String name, String type, Long householdId) {
        return sendAuthorizedRequest(new CreateCategoryRequest(name, type, householdId));
    }

    public Result<CategoryDTO> getAll(Long householdId) {
        return sendAuthorizedRequest(new GetCategoriesRequest(RequestAction.GET_CATEGORIES, householdId));
    }

    public Result<CategoryDTO> getIncomeCategory(Long householdId) {
        return sendAuthorizedRequest(new GetCategoriesRequest(RequestAction.GET_INCOME_CATEGORIES, householdId));
    }

    public Result<CategoryDTO> getExpenseCategory(Long householdId) {
        return sendAuthorizedRequest(new GetCategoriesRequest(RequestAction.GET_EXPENSE_CATEGORIES, householdId));
    }

    public Result<Void> update(Long id, String name, String type) {
        return sendAuthorizedRequest(new UpdateCategoryRequest(id, name, type));
    }

    public Result<Void> delete(Long categoryId) {
        return sendAuthorizedRequest(new DeleteCategoryRequest(categoryId));
    }
}
