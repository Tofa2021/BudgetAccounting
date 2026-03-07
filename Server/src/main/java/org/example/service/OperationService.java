package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.TransactionUtils;
import org.example.dao.OperationDAO;
import org.example.dao.UserDAO;
import org.example.exception.UserNotFoundException;
import org.example.model.Operation;
import org.example.model.User;

import java.util.List;

@RequiredArgsConstructor
public class OperationService {
    private final OperationDAO operationDAO;
    private final UserDAO userDAO;

    public List<Operation> getAllByUserId(Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            User user = userDAO.findById(session, userId).orElseThrow(() -> new UserNotFoundException(userId));
            return operationDAO.findAllByUserId(session, userId);
        });
    }
}
