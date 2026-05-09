package org.example.dto.request.category;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class UpdateCategoryRequest extends AuthorizedRequest {
    private final Long id;
    private final String name;
    private final String type;

    public UpdateCategoryRequest(Long id, String name, String type) {
        super(RequestAction.UPDATE_CATEGORY);
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
