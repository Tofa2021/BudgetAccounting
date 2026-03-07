package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.client.RRManager;
import org.example.dto.model.OperationDTO;

@Getter
public class OperationHistoryViewModel extends BaseViewModel {
    private final ObservableList<OperationDTO> operations = FXCollections.observableArrayList();

    public OperationHistoryViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
        refreshOperations();
    }

    private void refreshOperations() {
        var result = rrManager.getUserOperations();
        if (result.isSuccess()) {
            operations.setAll(result.getData().reversed());
        }
    }
}
