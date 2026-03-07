package org.example.client.viewModel;

import org.example.client.RRManager;
import org.example.dto.model.OperationDTO;

import java.util.List;

public class OperationHistoryViewModel extends BaseViewModel {
    private List<OperationDTO> operations;

    public OperationHistoryViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
        refreshOperations();
    }

    private void refreshOperations() {
        operations = rrManager.getUserOperations().getData();
        System.out.println(operations);
    }
}
