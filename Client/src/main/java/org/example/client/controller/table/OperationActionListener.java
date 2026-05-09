package org.example.client.controller.table;

import org.example.dto.OperationDTO;

public interface OperationActionListener {
    void onOperationUpdated(OperationDTO operationDTO);

    void onOperationDeleted(OperationDTO operationDTO);
}
