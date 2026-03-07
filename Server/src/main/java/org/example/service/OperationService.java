package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.OperationDAO;
import org.example.dto.request.AuthorizedModelIdRequest;
import org.example.exception.OperationNotFoundException;
import org.example.model.Operation;
import org.example.util.TransactionUtils;

import java.util.List;

@RequiredArgsConstructor
public class OperationService {
    private final OperationDAO operationDAO;

    public List<Operation> getAllByUserId(Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            return operationDAO.findAllByUserId(session, userId);
        });
    }

    public void deleteById(AuthorizedModelIdRequest request) {
        TransactionUtils.executeInTransaction(session -> {
            Long id = request.getId();
            operationDAO.findById(session, id).orElseThrow(() -> new OperationNotFoundException(id));
            operationDAO.deleteById(session, request.getId());
        });
    }
}
