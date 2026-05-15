package org.example;

import org.example.application.service.*;
import org.example.domain.dao.*;
import org.example.infrastructure.dao.*;
import org.example.infrastructure.security.BCryptPasswordEncoder;
import org.example.infrastructure.security.JwtProvider;
import org.example.infrastructure.security.PasswordEncoder;
import org.example.infrastructure.security.TokenProvider;
import org.example.infrastructure.transaction.HibernatePersistenceManager;
import org.example.presentation.RequestProcessor;
import org.example.presentation.connection.ConnectionManager;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.dtoMapper.DTOMapperImpl;
import org.example.presentation.requestHandler.*;

public class Main {
    static void main(String[] args) {
        DTOMapper dtoMapper = new DTOMapperImpl();

        HibernatePersistenceManager transactionManager = new HibernatePersistenceManager();

        AccountMemberDAO accountMemberDAO = new HibernateAccountMemberDAO(transactionManager);
        AccountDAO accountDAO = new HibernateAccountDAO(transactionManager);
        CategoryDAO categoryDAO = new HibernateCategoryDAO(transactionManager);
        HouseholdDAO householdDAO = new HibernateHouseholdDAO(transactionManager);
        HouseholdMemberDAO householdMemberDAO = new HibernateHouseholdMemberDAO(transactionManager);
        OperationDAO operationDAO = new HibernateOperationDAO(transactionManager);
        UserDAO userDAO = new HibernateUserDAO(transactionManager);

        TokenProvider tokenProvider = new JwtProvider();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        HouseholdPermissionChecker householdPermissionChecker = new HouseholdPermissionChecker(householdMemberDAO);
        AccountPermissionChecker accountPermissionChecker = new AccountPermissionChecker(accountMemberDAO);

        AccountMemberService accountMemberService = new AccountMemberService(
                transactionManager,
                accountPermissionChecker,
                accountMemberDAO,
                accountDAO,
                householdMemberDAO
        );
        AccountService accountService = new AccountService(
                transactionManager,
                householdPermissionChecker,
                accountDAO,
                accountMemberDAO,
                householdDAO,
                householdMemberDAO
        );
        CategoryService categoryService = new CategoryService(
                transactionManager,
                householdPermissionChecker,
                categoryDAO,
                householdDAO
        );
        HouseholdMemberService householdMemberService = new HouseholdMemberService(
                transactionManager,
                householdPermissionChecker,
                householdMemberDAO,
                householdDAO,
                userDAO
        );
        HouseholdService householdService = new HouseholdService(
                transactionManager,
                householdPermissionChecker,
                householdDAO,
                householdMemberDAO,
                userDAO,
                accountDAO
        );
        OperationService operationService = new OperationService(
                transactionManager,
                householdPermissionChecker,
                accountPermissionChecker,
                operationDAO,
                categoryDAO,
                accountDAO,
                accountMemberDAO,
                userDAO,
                householdMemberDAO
        );
        UserService userService = new UserService(
                transactionManager,
                userDAO,
                passwordEncoder,
                tokenProvider
        );

        AccountMemberRequestHandler accountMemberRequestHandler = new AccountMemberRequestHandler(dtoMapper, accountMemberService);
        AccountRequestHandler accountRequestHandler = new AccountRequestHandler(dtoMapper, accountService);
        AuthRequestHandler authRequestHandler = new AuthRequestHandler(userService);
        CategoryRequestHandler categoryRequestHandler = new CategoryRequestHandler(dtoMapper, categoryService);
        HouseholdMemberRequestHandler householdMemberRequestHandler = new HouseholdMemberRequestHandler(dtoMapper, householdMemberService);
        HouseholdRequestHandler householdRequestHandler = new HouseholdRequestHandler(dtoMapper, householdService);
        OperationRequestHandler operationRequestHandler = new OperationRequestHandler(dtoMapper, operationService);
        UserRequestHandler userRequestHandler = new UserRequestHandler(dtoMapper, userService);

        RequestProcessor requestProcessor = new RequestProcessor(
                tokenProvider,
                accountMemberRequestHandler,
                accountRequestHandler,
                authRequestHandler,
                categoryRequestHandler,
                householdMemberRequestHandler,
                householdRequestHandler,
                operationRequestHandler,
                userRequestHandler
        );

        ConnectionManager connectionManager = new ConnectionManager(requestProcessor);

        connectionManager.start();
    }
}
