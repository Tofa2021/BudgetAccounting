package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteHouseholdMemberRequest extends AuthorizedRequest {
    private final Long memberId;

    public DeleteHouseholdMemberRequest(Long memberId) {
        super(RequestAction.DELETE_HOUSEHOLD_MEMBER);
        this.memberId = memberId;
    }
}
