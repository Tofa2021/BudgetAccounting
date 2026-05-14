package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.OperationClient;
import org.example.dto.OperationDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class OperationViewModel extends BaseViewModel {
    private final OperationClient operationClient;
    private final SessionContext sessionContext;
    private final ObservableList<OperationDTO> operations = FXCollections.observableArrayList();

    @Override
    public void onViewShown() {
        refreshOperations();
    }

    public void refreshOperations() {
        var result = operationClient.getHouseholdOperations(sessionContext.getCurrentHousehold().getId());
        if (result.isSuccess()) {
            operations.setAll(result.getData().stream()
                    .sorted(Comparator.comparing(OperationDTO::getDateTime))
                    .toList()
                    .reversed());
        }
    }

    public void delete(OperationDTO operationDTO) {
        var result = operationClient.delete(operationDTO.getId());
        if (result.isSuccess()) {
            operations.remove(operationDTO);
        }
    }

    public void update(OperationDTO operationDTO) {
        if (operationClient.update(operationDTO).isSuccess()) {
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
        Instant dateFrom = Instant.now().minus(days, ChronoUnit.DAYS);
        var result = operationClient.getHouseholdRecentOperations(sessionContext.getCurrentHousehold().getId(), dateFrom);
        if (result.isSuccess()) {
            operations.setAll(result.getData());
        }
    }

    public void getFilteredOperations(
            Long userId,
            Long householdId,
            Long categoryId,
            String type,
            Instant dateFrom,
            Instant dateTo,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Integer limit
    ) {
        var result = operationClient.getFilteredOperations(userId, householdId, categoryId, type, minAmount, maxAmount, dateTo, dateFrom, limit);
        if (result.isSuccess()) {
            operations.setAll(result.getData());
        }
    }
}
