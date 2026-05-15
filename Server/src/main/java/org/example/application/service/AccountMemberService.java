package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.dao.PersistenceManager;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.LastRoleException;
import org.example.domain.exception.already_exists.AccountMemberAlreadyExistsException;
import org.example.domain.exception.forbidden.ForbiddenException;
import org.example.domain.exception.not_found.AccountMemberNotFoundException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.model.Account;
import org.example.domain.model.AccountMember;
import org.example.domain.model.AccountMemberRole;
import org.example.domain.model.HouseholdMember;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class AccountMemberService {
    private final PersistenceManager persistenceManager;
    private final AccountPermissionChecker accountPermissionChecker;
    private final AccountMemberDAO accountMemberDAO;
    private final AccountDAO accountDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public AccountMember create(Long householdMemberId, Long accountId, String role, Long userId) {
        log.debug("Creating account member with householdMemberId = {} accountId = {} role = {}", householdMemberId, accountId, role);

        return persistenceManager.executeTransaction(() -> {
            HouseholdMember householdMember = householdMemberDAO.findById(householdMemberId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(householdMemberId));

            AccountMember creatorMember = accountMemberDAO.findByAccountIdAndUserId(accountId, userId)
                    .orElseThrow(() -> new AccountMemberNotFoundException(accountId, userId));

            if (creatorMember.getRole() != AccountMemberRole.MANAGER) {
                throw new ForbiddenException("Only MANAGER can create account members");
            }

            if (accountMemberDAO.existsByAccountIdAndHouseholdMemberId(accountId, householdMemberId)) {
                throw new AccountMemberAlreadyExistsException(accountId, householdMemberId);
            }

            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));

            if (!householdMember.getHousehold().getId().equals(account.getHousehold().getId())) {
                throw new BadParameterException("Household member does not belong to this account's household");
            }

            AccountMember accountMember = new AccountMember();
            accountMember.setHouseholdMember(householdMember);
            accountMember.setAccount(account);
            accountMember.setRole(AccountMemberRole.fromString(role));

            accountMemberDAO.save(accountMember);

            log.info("Account member created with id = {} householdMemberId = {} accountId = {} role = {}",
                    accountMember.getId(), householdMemberId, accountId, role);
            return accountMember;
        });
    }

    public void updateRole(Long memberId, String role, Long userId) {
        log.debug("Updating role for account member with id = {} newRole = {}", memberId, role);

        persistenceManager.executeTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(memberId)
                    .orElseThrow(() -> new AccountMemberNotFoundException(memberId));

            AccountMemberRole oldRole = member.getRole();
            Long accountId = member.getAccount().getId();

            accountPermissionChecker.checkRole(accountId, userId, AccountMemberRole.MANAGER);

            boolean isSelfUpdate = Objects.equals(member.getHouseholdMember().getUser().getId(), userId);
            if (isSelfUpdate) {
                if (isLastManager(member)) {
                    throw new LastRoleException(member.getId(), accountId, AccountMemberRole.MANAGER);
                }
            } else {
                if (oldRole == AccountMemberRole.MANAGER) {
                    throw new ForbiddenException("MANAGER cannot update account member role another MANAGER");
                }
            }

            member.setRole(AccountMemberRole.fromString(role));

            accountMemberDAO.save(member);
            log.info("Account member role updated for member with id = {} oldRole = {} newRole = {}", memberId, oldRole, role);
        });
    }

    public void delete(Long id, Long userId) {
        log.debug("Deleting account member with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(id)
                    .orElseThrow(() -> new AccountMemberNotFoundException(id));

            Long accountId = member.getAccount().getId();

            boolean isSelfDelete = Objects.equals(member.getHouseholdMember().getUser().getId(), userId);
            if (isSelfDelete) {
                if (isLastManager(member)) {
                    throw new LastRoleException(id, accountId, AccountMemberRole.MANAGER);
                }
            } else {
                accountPermissionChecker.checkRole(accountId, userId, AccountMemberRole.MANAGER);

                if (member.getRole() == AccountMemberRole.MANAGER) {
                    throw new ForbiddenException("MANAGER cannot delete another MANAGER");
                }
            }

            accountMemberDAO.delete(member);
            log.info("Account member deleted with id = {}", id);
        });
    }

    private boolean isLastManager(AccountMember member) {
        if (member.getRole() != AccountMemberRole.MANAGER) {
            return false;
        }

        Long accountId = member.getAccount().getId();
        long managersCount = accountMemberDAO.countByAccountIdAndRole(accountId, AccountMemberRole.MANAGER);
        return managersCount == 1;
    }
}
