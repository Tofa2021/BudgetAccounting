package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.PersistenceManager;
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

@Slf4j
@RequiredArgsConstructor
public class AccountMemberService { // TODO check rights
    private final PersistenceManager persistenceManager;
    private final AccountMemberDAO accountMemberDAO;
    private final AccountDAO accountDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public AccountMember create(Long householdMemberId, Long accountId, String role) {
        log.debug("Creating account member with householdMemberId = {} accountId = {} role = {}", householdMemberId, accountId, role);

        return persistenceManager.executeTransaction(() -> {
            HouseholdMember householdMember = householdMemberDAO.findById(householdMemberId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(householdMemberId));
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));

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

    private void checkLastManager(AccountMember member) {
        if (member.getRole() == AccountMemberRole.MANAGER) {
            Long accountId = member.getAccount().getId();

            long adminCount = accountMemberDAO.countByAccountIdAndRole(accountId, AccountMemberRole.MANAGER);
            if (adminCount == 1) {
                throw new LastManagerException(accountId, member.getId());
            }
        }
    }

    public void updateRole(Long memberId, String role) {
        log.debug("Updating role for account member with id = {} newRole = {}", memberId, role);

        persistenceManager.executeTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(memberId)
                    .orElseThrow(() -> new AccountMemberNotFound(memberId));

            AccountMemberRole oldRole = member.getRole();

            checkLastManager(member);

            member.setRole(AccountMemberRole.fromString(role));

            accountMemberDAO.save(member);
            log.info("Account member role updated for member with id = {} oldRole = {} newRole = {}", memberId, oldRole, role);
        });
    }

    public void delete(Long id) {
        log.debug("Deleting account member with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            AccountMember member = accountMemberDAO.findById(id)
                    .orElseThrow(() -> new AccountMemberNotFound(id));
            checkLastManager(member);
            accountMemberDAO.delete(member);
            log.info("Account member deleted with id = {}", id);
        });
    }
}
