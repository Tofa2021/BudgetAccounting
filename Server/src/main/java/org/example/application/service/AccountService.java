package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.exception.AccessDeniedException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.model.*;
import org.example.dto.request.account.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class AccountService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final AccountDAO accountDAO;
    private final AccountMemberDAO accountMemberDAO;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public Account create(CreateAccountRequest request, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            Household household = householdDAO.findById(request.getHouseholdId())
                    .orElseThrow(() -> new HouseholdNotFoundException(request.getHouseholdId()));

            HouseholdMember householdMember = householdMemberDAO
                    .findByUserIdAndHouseholdId(userId, household.getId())
                    .orElseThrow(() -> new AccessDeniedException("Not a member of this household"));

            Account account = new Account();
            account.setName(request.getName());
            account.setCurrency(request.getCurrency());
            account.setHousehold(household);
            account.setAmount(BigDecimal.ZERO);
            accountDAO.save(account);

            AccountMember accountMember = new AccountMember();
            accountMember.setAccount(account);
            accountMember.setHouseholdMember(householdMember);
            accountMember.setRole(AccountMemberRole.MANAGER);
            accountMemberDAO.save(accountMember);

            return account;
        });
    }

    public Account getAccount(GetAccountRequest request, Long userId) {
        Account account = accountDAO.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

        boolean hasAccess = accountMemberDAO.existsByAccountIdAndUserId(account.getId(), userId);
        if (!hasAccess) {
            throw new AccessDeniedException("No access to this account");
        }

        return account;
    }

    public List<Account> getAccounts(GetAccountsRequest request, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            List<AccountMember> members = accountMemberDAO.findByUserIdAndHouseholdId(userId, request.getHouseholdId());
            return members
                    .stream()
                    .map(AccountMember::getAccount)
                    .toList();
        });
    }

    public void update(UpdateAccountRequest request) {
        transactionManager.executeInTransaction(() -> {
            Account account = accountDAO.findById(request.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));

            if (request.getName() != null) {
                account.setName(request.getName());
            }

            if (request.getCurrency() != null) {
                account.setCurrency(request.getCurrency());
            }

            accountDAO.save(account);
        });
    }

    public void delete(DeleteAccountRequest request) {
        transactionManager.executeInTransaction(() -> {
            accountDAO.deleteById(request.getAccountId());
        });
    }
}
