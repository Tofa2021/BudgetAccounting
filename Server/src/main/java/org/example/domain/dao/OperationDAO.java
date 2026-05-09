package org.example.domain.dao;

import org.example.domain.model.Operation;

import java.util.List;

public interface OperationDAO extends DAO<Operation, Long> {
    List<Operation> find(OperationFilter filter);
}

