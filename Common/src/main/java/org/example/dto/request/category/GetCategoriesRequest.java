package org.example.dto.request.category;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetCategoriesRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetCategoriesRequest(RequestAction action, String token, Long householdId) {
        super(action, token);
        this.householdId = householdId;
    }
}
