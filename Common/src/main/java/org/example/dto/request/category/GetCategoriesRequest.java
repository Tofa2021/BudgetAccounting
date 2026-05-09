package org.example.dto.request.category;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetCategoriesRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetCategoriesRequest(RequestAction action, Long householdId) {
        super(action);
        this.householdId = householdId;
    }
}
