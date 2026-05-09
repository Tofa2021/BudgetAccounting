package org.example.dto.request.account_member;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class UpdateAccountMemberRoleRequest extends AuthorizedRequest {
    private final Long memberId;
    private final String role;

    public UpdateAccountMemberRoleRequest(Long memberId, String role) {
        super(RequestAction.UPDATE_ACCOUNT_MEMBER_ROLE);
        this.memberId = memberId;
        this.role = role;
    }
}
