package org.example.domain.exception.not_found;

public class AccountMemberNotFoundException extends NotFoundException {
    public AccountMemberNotFoundException(Long memberId) {
        super("AccountMember", "id", String.valueOf(memberId));
    }

    public AccountMemberNotFoundException(Long accountId, Long userId) {
        super("AccountMember with accountId = " + accountId + "userId = " + userId + " not found");
    }
}
