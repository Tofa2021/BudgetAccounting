package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.*;
import org.example.domain.exception.forbidden.ForbiddenException;
import org.example.domain.exception.forbidden.RoleRequiredException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.model.*;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final PersistenceManager persistenceManager;
    private final HouseholdPermissionChecker householdPermissionChecker;
    private final AccountDAO accountDAO;
    private final AccountMemberDAO accountMemberDAO;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public Account create(Long householdId, String name, Currency currency, Long userId) {
        log.debug("Creating account with householdId = {} name = {} currency = {} userId = {}", householdId, name, currency, userId);

        return persistenceManager.executeTransaction(() -> {
            HouseholdMember householdMember = householdMemberDAO
                    .findByUserIdAndHouseholdId(userId, householdId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            HouseholdMemberRole role = householdMember.getRole();
            if (role != HouseholdMemberRole.OWNER && role != HouseholdMemberRole.MANAGER) {
                throw new RoleRequiredException(role, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);
            }

            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

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
                    account.getId(), householdId, name, currency, userId);
            return account;
        });
    }

    public Account getAccount(Long id, Long userId) {
        log.info("Getting account with id = {}", id);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            Account account = accountDAO.findByIdWithRelations(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            Long householdId = account.getHousehold().getId();

            HouseholdMemberRole role = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            if (role == HouseholdMemberRole.MEMBER) {
                boolean hasAccess = accountMemberDAO.existsByAccountIdAndUserId(id, userId);
                if (!hasAccess) {
                    throw new ForbiddenException("Member with role = " + role + "  has no access to account with id = " + id
                            + " because there is no membership");
                }
            }

            log.debug("Account gotten id={}, name={}", account.getId(), account.getName());
            return account;
        });
    }

    public List<Account> getAccounts(Long householdId, Long userId) {
        log.debug("Getting accounts with householdId={}, userId={}", householdId, userId);

        return persistenceManager.executeReadOnly(() -> {
            HouseholdMemberRole role = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            List<Account> accounts;
            if (role == HouseholdMemberRole.MEMBER) {
                accounts = accountDAO.getMemberAccountsWithRelations(householdId, userId);
            } else {
                accounts = accountDAO.getAllByHouseholdIdWithRelations(householdId);
            }

            log.debug("Accounts gotten householdId={}, count={}, userId={}",
                    householdId, accounts.size(), userId);
            return accounts;
        });
    }

    public void update(Long id, String name, Currency currency, Long userId) {
        log.debug("Updating account with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            Account account = accountDAO.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            Long householdId = account.getHousehold().getId();
            householdPermissionChecker.checkRole(householdId, userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

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

    public void delete(Long id, Long userId) {
        log.debug("Deleting account with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            Account account = accountDAO.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException(id));

            Long householdId = account.getHousehold().getId();
            householdPermissionChecker.checkRole(householdId, userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            accountDAO.deleteById(id);
            log.info("Account deleted with id = {}", id);
        });
    }
}
