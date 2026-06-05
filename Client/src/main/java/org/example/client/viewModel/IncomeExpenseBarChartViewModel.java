package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.client.SessionContext;
import org.example.client.connection.api.OperationClient;
import org.example.dto.HouseholdDTO;
import org.example.dto.OperationDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class IncomeExpenseBarChartViewModel extends BaseViewModel {
    private final SessionContext sessionContext;
    private final OperationClient operationClient;

    @Getter
    private final ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();

    private List<OperationDTO> allOperations;

    @Override
    public void onViewShown() {
        loadAllOperations();
    }

    private void refreshData() {
        if (allOperations == null || allOperations.isEmpty()) {
            series.clear();
            return;
        }

        Map<String, MonthlyData> monthlyData = calculateLast12MonthsData();

        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Доходы");

        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Расходы");

        List<String> sortedMonths = new ArrayList<>(monthlyData.keySet());
        sortedMonths.sort((a, b) -> {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("ru"));
                LocalDate dateA = LocalDate.parse("01 " + a, DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("ru")));
                LocalDate dateB = LocalDate.parse("01 " + b, DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("ru")));
                return dateA.compareTo(dateB);
            } catch (Exception e) {
                return a.compareTo(b);
            }
        });

        for (String monthLabel : sortedMonths) {
            MonthlyData data = monthlyData.get(monthLabel);
            incomeSeries.getData().add(new XYChart.Data<>(monthLabel, data.income));
            expenseSeries.getData().add(new XYChart.Data<>(monthLabel, data.expense));
        }

        series.clear();
        series.addAll(incomeSeries, expenseSeries);
    }

    private Map<String, MonthlyData> calculateLast12MonthsData() {
        Map<String, MonthlyData> result = new LinkedHashMap<>();

        LocalDate now = LocalDate.now();

        for (int i = 11; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthLabel = getMonthLabel(date);
            result.put(monthLabel, new MonthlyData(BigDecimal.ZERO, BigDecimal.ZERO));
        }

        LocalDate startDate = now.minusMonths(12).withDayOfMonth(1);

        List<OperationDTO> filteredOperations = allOperations.stream()
                .filter(op -> {
                    LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !opDate.isBefore(startDate) && !opDate.isAfter(now);
                })
                .collect(Collectors.toList());

        for (OperationDTO op : filteredOperations) {
            LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
            String monthLabel = getMonthLabel(opDate);

            MonthlyData data = result.get(monthLabel);
            if (data != null) {
                BigDecimal amount = op.getAmount();
                if ("INCOME".equals(op.getType())) {
                    data.income = data.income.add(amount);
                } else if ("EXPENSE".equals(op.getType())) {
                    data.expense = data.expense.add(amount);
                }
            }
        }

        return result;
    }

    private String getMonthLabel(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("ru"));
        return date.format(formatter);
    }

    private void loadAllOperations() {
        HouseholdDTO household = sessionContext.getCurrentHousehold().get();
        if (household == null) {
            showError("Нет домохозяйства");
            return;
        }

        var result = operationClient.getHouseholdOperations(household.getId());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        allOperations = result.getData();
        refreshData();
    }

    @Getter
    @Setter
    private static class MonthlyData {
        private BigDecimal income;
        private BigDecimal expense;

        public MonthlyData(BigDecimal income, BigDecimal expense) {
            this.income = income;
            this.expense = expense;
        }
    }
}