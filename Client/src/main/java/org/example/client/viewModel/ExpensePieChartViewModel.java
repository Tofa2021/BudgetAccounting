package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.OperationClient;
import org.example.dto.HouseholdDTO;
import org.example.dto.OperationDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ExpensePieChartViewModel extends BaseViewModel {
    private final SessionContext sessionContext;
    private final OperationClient operationClient;
    @Getter
    private final ObservableList<PieChart.Data> expensePieData = FXCollections.observableArrayList();
    @Getter
    private final ObjectProperty<Period> currentPeriod = new SimpleObjectProperty<>();
    private List<OperationDTO> allExpenses;

    @Override
    public void onViewShown() {
        refreshAllExpenses();
        expensePieData.setAll(convertAllToPieData(allExpenses));

        currentPeriod.addListener((obs, old, period) -> {
            if (period != null) {
                refreshDataForPeriod(period);
            }
        });
    }

    private PieChart.Data convertToPieData(String categoryName, List<OperationDTO> operations) {
        return new PieChart.Data(categoryName, operations.stream()
                .map(OperationDTO::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .doubleValue()
        );
    }

    private List<PieChart.Data> convertAllToPieData(List<OperationDTO> operations) {
        return operations.stream()
                .collect(Collectors.groupingBy(OperationDTO::getCategoryName))
                .entrySet().stream()
                .map(entry -> convertToPieData(entry.getKey(), entry.getValue()))
                .toList();
    }

    private void setExpensePieData(List<OperationDTO> operations) {
        expensePieData.setAll(convertAllToPieData(operations));
    }

    private void refreshDataForPeriod(Period period) {
        if (period == Period.ALL) {
            setExpensePieData(allExpenses);
            return;
        }

        Instant dateFrom = calculateDateFrom(period);
        List<OperationDTO> operations = allExpenses.stream()
                .filter(o -> o.getDateTime().isAfter(dateFrom))
                .toList();
        setExpensePieData(operations);
    }

    private Instant calculateDateFrom(Period period) {
        if (period == Period.ALL) {
            throw new IllegalArgumentException("Cannot calculate dateFrom to Period.ALL");
        }

        return Instant.now().minus(calculateDays(period), ChronoUnit.DAYS);
    }

    private int calculateDays(Period period) {
        return switch (period) {
            case DAY -> 1;
            case WEEK -> 7;
            case MONTH -> 30;
            case YEAR -> 365;
            case ALL -> throw new IllegalArgumentException("Cannot calculate days to Period.ALL");
        };
    }

    private void refreshAllExpenses() {
        HouseholdDTO currentHousehold = sessionContext.getCurrentHousehold().get();
        if (currentHousehold == null) {
            showError("Нет домохозяйства");
            return;
        }

        var result = operationClient.getExpenses(currentHousehold.getId(), Instant.now().minus(7, ChronoUnit.DAYS));
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        allExpenses = result.getData();
    }
}
