package org.example;

import org.example.application.service.BudgetService;
import org.example.application.service.OperationService;
import org.example.application.service.UserService;
import org.example.domain.dao.BudgetDAO;
import org.example.domain.dao.OperationDAO;
import org.example.domain.dao.RoleDAO;
import org.example.domain.dao.UserDAO;
import org.example.infrastructure.dao.HibernateBudgetDAO;
import org.example.infrastructure.dao.HibernateOperationDAO;
import org.example.infrastructure.dao.HibernateRoleDAO;
import org.example.infrastructure.dao.HibernateUserDAO;
import org.example.infrastructure.security.BCryptPasswordEncoder;
import org.example.infrastructure.security.JwtProvider;
import org.example.infrastructure.security.PasswordEncoder;
import org.example.infrastructure.security.TokenProvider;
import org.example.infrastructure.transaction.HibernateTransactionManager;
import org.example.presentation.RequestProcessor;
import org.example.presentation.connection.ConnectionManager;

public class Main {
    public static void main(String[] args) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();

        BudgetDAO budgetDAO = new HibernateBudgetDAO(transactionManager);
        RoleDAO roleDAO = new HibernateRoleDAO(transactionManager);
        UserDAO userDAO = new HibernateUserDAO(transactionManager);
        OperationDAO operationDAO = new HibernateOperationDAO(transactionManager);

        TokenProvider tokenProvider = new JwtProvider();

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        BudgetService budgetService = new BudgetService(budgetDAO, operationDAO, userDAO, transactionManager);
        UserService userService = new UserService(passwordEncoder, tokenProvider, userDAO, roleDAO, budgetDAO, transactionManager);
        OperationService operationService = new OperationService(operationDAO, budgetDAO, transactionManager);

        RequestProcessor requestProcessor = new RequestProcessor(tokenProvider, budgetService, userService, operationService);

        ConnectionManager connectionManager = new ConnectionManager(requestProcessor);

        connectionManager.start();
    }
}
