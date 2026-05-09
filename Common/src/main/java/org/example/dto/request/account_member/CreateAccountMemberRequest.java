package org.example.dto.request.account_member;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class CreateAccountMemberRequest extends AuthorizedRequest {
    private final Long householdMemberId;
    private final Long accountId;
    private final String role;

    public CreateAccountMemberRequest(Long householdMemberId, Long accountId, String role) {
        super(RequestAction.CREATE_ACCOUNT_MEMBER);
        this.householdMemberId = householdMemberId;
        this.accountId = accountId;
        this.role = role;
    }
}
