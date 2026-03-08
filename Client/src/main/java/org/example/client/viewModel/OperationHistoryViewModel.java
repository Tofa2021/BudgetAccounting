package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.client.RRManager;
import org.example.dto.model.OperationDTO;

import java.util.Comparator;
import java.util.Objects;

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

    public void refreshOperations() {
        var result = rrManager.getUserOperations();
        if (result.isSuccess()) {
            operations.setAll(result.getData().stream()
                    .sorted(Comparator.comparing(OperationDTO::getDateTime))
                    .toList()
                    .reversed());
        }
    }

    public void delete(OperationDTO operationDTO) {
        var result = rrManager.deleteOperation(operationDTO.getId());
        if (result.isSuccess()) {
            operations.remove(operationDTO);
        }
    }

    public void update(OperationDTO operationDTO) {
        if (rrManager.updateOperation(operationDTO).isSuccess()) {
            operations.stream()
                    .filter(operationDTO1 -> Objects.equals(operationDTO1.getId(), operationDTO.getId()))
                    .findFirst()
                    .ifPresent(oldOperation -> {
                        int index = operations.indexOf(oldOperation);
                        operations.set(index, operationDTO);
                    });
        }
    }
}
