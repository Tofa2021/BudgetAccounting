package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.OperationDTO;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

@Getter
public class OperationHistoryViewModel extends BaseViewModel {
    private final ObservableList<OperationDTO> operations = FXCollections.observableArrayList();

    public OperationHistoryViewModel(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    @Override
    public void init() {
    }

    public void refreshOperations() {
        var result = serverInteractionManager.getUserOperations();
        if (result.isSuccess()) {
            operations.setAll(result.getData().stream()
                    .sorted(Comparator.comparing(OperationDTO::getDateTime))
                    .toList()
                    .reversed());
        }
    }

    public void delete(OperationDTO operationDTO) {
        var result = serverInteractionManager.deleteOperation(operationDTO.getId());
        if (result.isSuccess()) {
            operations.remove(operationDTO);
        }
    }

    public void update(OperationDTO operationDTO) {
        if (serverInteractionManager.updateOperation(operationDTO).isSuccess()) {
            operations.stream()
                    .filter(operationDTO1 -> Objects.equals(operationDTO1.getId(), operationDTO.getId()))
                    .findFirst()
                    .ifPresent(oldOperation -> {
                        int index = operations.indexOf(oldOperation);
                        operations.set(index, operationDTO);
                    });
        }
    }

    public void loadRecentOperations(int days) {
        var result = serverInteractionManager.getRecentOperations(days);
        if (result.isSuccess()) {
            operations.setAll(result.getData());
        }
    }

    public void getFilteredOperations(
            String type,
            String category,
            Instant dateFrom,
            Instant dateTo,
            Double minAmount,
            Double maxAmount
    ) {
        var result = serverInteractionManager.getFilteredOperations(type, category, dateFrom, dateTo, minAmount, maxAmount);
        if (result.isSuccess()) {
            operations.setAll(result.getData());
        }
    }

    public double getBalance() {
        var result = serverInteractionManager.getAmount();
        if (result.isSuccess()) {
            return result.getData();
        }
        throw new RuntimeException();
    }
}
