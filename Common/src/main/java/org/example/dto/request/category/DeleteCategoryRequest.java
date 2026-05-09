package org.example.dto.request.category;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteCategoryRequest extends AuthorizedRequest {
    private final Long categoryId;

    public DeleteCategoryRequest(Long categoryId) {
        super(RequestAction.DELETE_CATEGORY);
        this.categoryId = categoryId;
    }
}
