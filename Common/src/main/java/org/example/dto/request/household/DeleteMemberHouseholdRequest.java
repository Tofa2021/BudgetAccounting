package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteMemberHouseholdRequest extends AuthorizedRequest {
    private final Long memberId;

    public DeleteMemberHouseholdRequest(String token, Long memberId) {
        super(RequestAction.DELETE_HOUSEHOLD_MEMBER, token);
        this.memberId = memberId;
    }
}
