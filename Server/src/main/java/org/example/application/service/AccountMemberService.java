package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.exception.LastManagerException;
import org.example.domain.exception.not_found.AccountMemberNotFound;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.model.Account;
import org.example.domain.model.AccountMember;
import org.example.domain.model.AccountMemberRole;
import org.example.domain.model.HouseholdMember;
import org.example.dto.request.account_member.CreateAccountMemberRequest;
import org.example.dto.request.account_member.DeleteAccountMemberRequest;
import org.example.dto.request.account_member.UpdateAccountMemberRoleRequest;

@RequiredArgsConstructor
public class AccountMemberService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final AccountMemberDAO accountMemberDAO;
    private final AccountDAO accountDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public AccountMember create(CreateAccountMemberRequest request) {
        return transactionManager.executeInTransaction(() -> {
            HouseholdMember householdMember = householdMemberDAO.findById(request.getHouseholdMemberId())
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(request.getHouseholdMemberId()));
            Account account = accountDAO.findById(request.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

            AccountMember accountMember = new AccountMember();
            accountMember.setHouseholdMember(householdMember);
            accountMember.setAccount(account);
            accountMember.setRole(AccountMemberRole.fromString(request.getRole()));

            return accountMemberDAO.save(accountMember);
        });
    }

    private void checkLastManager(AccountMember member) {
        if (member.getRole() == AccountMemberRole.MANAGER) {
            Long accountId = member.getAccount().getId();

            int adminCount = accountMemberDAO.countByAccountIdAndRole(accountId, AccountMemberRole.MANAGER);
            if (adminCount == 1) {
                throw new LastManagerException(accountId, member.getId());
            }
        }
    }

    public void updateRole(UpdateAccountMemberRoleRequest request) {
        transactionManager.executeInTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(request.getMemberId())
                    .orElseThrow(() -> new AccountMemberNotFound(request.getMemberId()));

            checkLastManager(member);

            member.setRole(AccountMemberRole.fromString(request.getRole()));

            accountMemberDAO.save(member);
        });
    }

    public void delete(DeleteAccountMemberRequest request) {
        transactionManager.executeInTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(request.getMemberId())
                    .orElseThrow(() -> new AccountMemberNotFound(request.getMemberId()));
            checkLastManager(member);
            accountMemberDAO.delete(member);
        });
    }
}
