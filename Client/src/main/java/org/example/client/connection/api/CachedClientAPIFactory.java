package org.example.client.connection.api;

import lombok.RequiredArgsConstructor;
import org.example.client.connection.ServerInteractionManager;

@RequiredArgsConstructor
public class CachedClientAPIFactory {
    private final ServerInteractionManager serverInteractionManager;
    private AccountClient accountClient;
    private AccountMemberClient accountMemberClient;
    private CategoryClient categoryClient;
    private HouseholdClient householdClient;
    private HouseholdMemberClient householdMemberClient;
    private OperationClient operationClient;
    private UserClient userClient;
    private AuthClient authClient;

    public AuthClient createAuthClient() {
        if (authClient == null) {
            authClient = new AuthClient(serverInteractionManager);
        }
        return authClient;
    }

    public AccountClient createAccount() {
        if (accountClient == null) {
            accountClient = new AccountClient(serverInteractionManager);
        }
        return accountClient;
    }

    public AccountMemberClient createAccountMember() {
        if (accountMemberClient == null) {
            accountMemberClient = new AccountMemberClient(serverInteractionManager);
        }
        return accountMemberClient;
    }

    public CategoryClient createCategory() {
        if (categoryClient == null) {
            categoryClient = new CategoryClient(serverInteractionManager);
        }
        return categoryClient;
    }

    public HouseholdClient createHouseholdClient() {
        if (householdClient == null) {
            householdClient = new HouseholdClient(serverInteractionManager);
        }
        return householdClient;
    }

    public HouseholdMemberClient createHouseholdMemberClient() {
        if (householdMemberClient == null) {
            householdMemberClient = new HouseholdMemberClient(serverInteractionManager);
        }
        return householdMemberClient;
    }

    public OperationClient createOperationClient() {
        if (operationClient == null) {
            operationClient = new OperationClient(serverInteractionManager);
        }
        return operationClient;
    }

    public UserClient createUserClient() {
        if (userClient == null) {
            userClient = new UserClient(serverInteractionManager);
        }
        return userClient;
    }

    public void clear() {
        accountClient = null;
        accountMemberClient = null;
        categoryClient = null;
        householdClient = null;
        householdMemberClient = null;
        operationClient = null;
        userClient = null;
        authClient = null;

    }
}
