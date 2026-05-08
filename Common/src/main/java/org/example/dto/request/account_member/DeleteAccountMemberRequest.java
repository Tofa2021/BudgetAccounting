package org.example.dto.request.account_member;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteAccountMemberRequest extends AuthorizedRequest {
    private final Long memberId;

    public DeleteAccountMemberRequest(String token, Long memberId) {
        super(RequestAction.DELETE_ACCOUNT_MEMBER, token);
        this.memberId = memberId;
    }
}
