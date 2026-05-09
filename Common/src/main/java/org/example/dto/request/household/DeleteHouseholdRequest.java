package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteHouseholdRequest extends AuthorizedRequest {
    private final Long id;

    public DeleteHouseholdRequest(Long id) {
        super(RequestAction.DELETE_HOUSEHOLD);
        this.id = id;
    }
}
