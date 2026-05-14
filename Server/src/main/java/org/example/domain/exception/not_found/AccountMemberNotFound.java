package org.example.domain.exception.not_found;

public class AccountMemberNotFound extends NotFoundException {
    public AccountMemberNotFound(Long memberId) {
        super("AccountMember", "id", String.valueOf(memberId));
    }

    public AccountMemberNotFound(Long accountId, Long userId) {
        super("AccountMember with accountId = " + accountId + "userId = " + userId + " not found");
    }
}
