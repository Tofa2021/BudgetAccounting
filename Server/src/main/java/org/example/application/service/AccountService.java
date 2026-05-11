package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.PersistenceManager;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.exception.AccessDeniedException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.model.*;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public class AccountService { // TODO check rights and TODO logging
    private final PersistenceManager persistenceManager;
    private final AccountDAO accountDAO;
    private final AccountMemberDAO accountMemberDAO;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public Account create(Long householdId, String name, Currency currency, Long userId) {
        return persistenceManager.executeTransaction(() -> {
            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

            HouseholdMember householdMember = householdMemberDAO
                    .findByUserIdAndHouseholdId(userId, household.getId())
                    .orElseThrow(() -> new AccessDeniedException("Not a member of this household"));

            Account account = new Account();
            account.setName(name);
            account.setCurrency(currency);
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

    public Account getAccount(Long id, Long userId) {
        return persistenceManager.executeReadOnlyTransaction(() -> {

            boolean hasAccess = accountMemberDAO.existsByAccountIdAndUserId(id, userId);
            if (!hasAccess) {
                throw new AccessDeniedException("No access to this account");
            }

            return accountDAO.findByIdWithRelations(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));
        });
    }

    public List<Account> getAccounts(Long householdId, Long userId) {
        return persistenceManager.executeReadOnly(() -> accountDAO.getAllByHouseholdIdWithRelations(householdId));
    }

    public void update(Long id, String name, Currency currency) {
        persistenceManager.executeTransaction(() -> {
            Account account = accountDAO.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            if (name != null) {
                account.setName(name);
            }

            if (currency != null) {
                account.setCurrency(currency);
            }

            accountDAO.save(account);
        });
    }

    public void delete(Long id) {
        persistenceManager.executeTransaction(() -> {
            accountDAO.deleteById(id);
        });
    }
}
