package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
public class AccountService { // TODO check rights
    private final PersistenceManager persistenceManager;
    private final AccountDAO accountDAO;
    private final AccountMemberDAO accountMemberDAO;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public Account create(Long householdId, String name, Currency currency, Long userId) {
        log.debug("Creating account with householdId = {} name = {} currency = {} userId = {}", householdId, name, currency, userId);

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

            log.info("Account created with id = {} householdId = {} name = {} currency = {} userId = {}",
                    accountMember.getId(), householdId, name, currency, userId);
            return account;
        });
    }

    public Account getAccount(Long id, Long userId) {// TODO check role
        log.info("Getting account with id = {}", id);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            boolean hasAccess = accountMemberDAO.existsByAccountIdAndUserId(id, userId);
            if (!hasAccess) {
                throw new AccessDeniedException("No access to this account");
            }

            Account account = accountDAO.findByIdWithRelations(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            log.info("Account gotten id={}, name={}", account.getId(), account.getName());
            return account;
        });
    }

    public List<Account> getAccounts(Long householdId, Long userId) { // TODO check role
        log.debug("Getting accounts with householdId={}, userId={}", householdId, userId);

        return persistenceManager.executeReadOnly(() -> {
            List<Account> accounts = accountDAO.getAllByHouseholdIdWithRelations(householdId);
            log.info("Accounts gotten householdId={}, count={}, userId={}",
                    householdId, accounts.size(), userId);
            return accounts;
        });
    }

    public void update(Long id, String name, Currency currency) {
        log.debug("Updating account with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            Account account = accountDAO.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            String oldName = account.getName();
            Currency oldCurrency = account.getCurrency();

            if (name != null) {
                account.setName(name);
            }

            if (currency != null) {
                account.setCurrency(currency);
            }

            accountDAO.save(account);
            log.info("Account updated with id = {} oldName = {} newName = {} oldCurrency = {} newCurrency = {}",
                    id, oldName, name, oldCurrency, currency);
        });
    }

    public void delete(Long id) {
        log.debug("Deleting account with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            accountDAO.deleteById(id);
            log.info("Account deleted with id = {}", id);
        });
    }
}
