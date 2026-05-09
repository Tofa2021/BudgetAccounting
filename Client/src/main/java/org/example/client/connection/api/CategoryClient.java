package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.CategoryDTO;
import org.example.request.RequestAction;

import java.util.Map;

public class CategoryClient extends BaseClient {
    public CategoryClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<CategoryDTO> create(String name, String type, Long householdId) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "name", name,
                        "type", type,
                        "householdId", householdId
                )
        );
    }

    public Result<CategoryDTO> getAll(Long householdId) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "householdId", householdId
                )
        );
    }

    public Result<CategoryDTO> getIncomeCategory(Long householdId) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "householdId", householdId
                )
        );
    }

    public Result<CategoryDTO> getExpenseCategory(Long householdId) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "householdId", householdId
                )
        );
    }

    public Result<Void> update(Long id, String name, String type) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "id", id,
                        "name", name,
                        "type", type
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.CREATE_CATEGORY,
                Map.of(
                        "id", id
                )
        );
    }
}
