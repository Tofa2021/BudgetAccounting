package org.example.dto.request.category;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class CreateCategoryRequest extends AuthorizedRequest {
    private final String name;
    private final String type;
    private final Long householdId;

    public CreateCategoryRequest(String name, String type, Long householdId) {
        super(RequestAction.CREATE_CATEGORY);
        this.name = name;
        this.type = type;
        this.householdId = householdId;
    }
}
