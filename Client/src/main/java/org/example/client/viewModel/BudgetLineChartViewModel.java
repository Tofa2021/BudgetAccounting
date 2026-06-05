package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import lombok.*;
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
public class BudgetLineChartViewModel extends BaseViewModel {
    private final SessionContext sessionContext;
    private final OperationClient operationClient;

    @Getter
    private final ObjectProperty<Period> currentPeriod = new SimpleObjectProperty<>();
    @Getter
    private final ObservableList<XYChart.Series<String, Number>> series = FXCollections.observableArrayList();

    private List<OperationDTO> allOperations;

    @Override
    public void onViewShown() {
        loadAllOperations();
        refreshDataForPeriod(Period.WEEK);

        currentPeriod.addListener((obs, old, period) -> {
            if (period != null && allOperations != null) {
                refreshDataForPeriod(period);
            }
        });
    }

    private void refreshDataForPeriod(Period period) {
        if (allOperations == null || allOperations.isEmpty()) {
            series.clear();
            return;
        }

        List<BudgetTimePoint> points = calculateBudgetTimePoints(period);
        convertToSeries(points, period);
    }

    private List<BudgetTimePoint> calculateBudgetTimePoints(Period period) {
        LocalDate startDate = getStartDate(period);
        LocalDate endDate = LocalDate.now();

        List<OperationDTO> filteredOperations = filterOperationsByDateRange(startDate, endDate);

        if (filteredOperations.isEmpty()) {
            return Collections.emptyList();
        }

        // Группируем в зависимости от выбранного периода
        Map<String, List<OperationDTO>> groupedData = groupOperationsByPeriodType(filteredOperations, period, startDate, endDate);

        List<BudgetTimePoint> points = new ArrayList<>();
        BigDecimal runningBalance = BigDecimal.ZERO;

        // Получаем отсортированные ключи
        List<String> sortedKeys = getSortedKeys(groupedData, period);

        for (String key : sortedKeys) {
            List<OperationDTO> ops = groupedData.get(key);

            BigDecimal periodIncome = ops.stream()
                    .filter(op -> "INCOME".equals(op.getType()))
                    .map(OperationDTO::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal periodExpense = ops.stream()
                    .filter(op -> "EXPENSE".equals(op.getType()))
                    .map(OperationDTO::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            runningBalance = runningBalance.add(periodIncome).subtract(periodExpense);

            points.add(new BudgetTimePoint(key, runningBalance, periodIncome, periodExpense));
        }

        return points;
    }

    private Map<String, List<OperationDTO>> groupOperationsByPeriodType(
            List<OperationDTO> operations,
            Period period,
            LocalDate startDate,
            LocalDate endDate) {

        Map<String, List<OperationDTO>> result = new LinkedHashMap<>();

        // Определяем формат группировки в зависимости от периода
        switch (period) {
            case DAY:
                // По часам
                return groupByHour(operations, startDate, endDate);
            case WEEK:
                // По дням
                return groupByDay(operations, startDate, endDate);
            case MONTH:
                // По неделям
                return groupByWeek(operations, startDate, endDate);
            case YEAR:
                // По месяцам
                return groupByMonth(operations, startDate, endDate);
            case ALL:
                // По месяцам
                return groupByMonth(operations, startDate, endDate);
            default:
                return groupByDay(operations, startDate, endDate);
        }
    }

    private Map<String, List<OperationDTO>> groupByHour(List<OperationDTO> operations, LocalDate startDate, LocalDate endDate) {
        Map<String, List<OperationDTO>> result = new LinkedHashMap<>();

        // Инициализируем все часы в диапазоне дат
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            for (int hour = 0; hour < 24; hour++) {
                String key = String.format("%s %02d:00", currentDate.format(DateTimeFormatter.ofPattern("dd.MM")), hour);
                result.put(key, new ArrayList<>());
            }
            currentDate = currentDate.plusDays(1);
        }

        // Группируем операции по часам
        for (OperationDTO op : operations) {
            LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
            int hour = op.getDateTime().atZone(ZoneId.systemDefault()).getHour();
            String key = String.format("%s %02d:00", opDate.format(DateTimeFormatter.ofPattern("dd.MM")), hour);

            if (result.containsKey(key)) {
                result.get(key).add(op);
            }
        }

        return result;
    }

    private Map<String, List<OperationDTO>> groupByDay(List<OperationDTO> operations, LocalDate startDate, LocalDate endDate) {
        Map<String, List<OperationDTO>> result = new LinkedHashMap<>();

        // Инициализируем все дни
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            String key = current.format(DateTimeFormatter.ofPattern("dd.MM"));
            result.put(key, new ArrayList<>());
            current = current.plusDays(1);
        }

        // Группируем операции по дням
        for (OperationDTO op : operations) {
            LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
            String key = opDate.format(DateTimeFormatter.ofPattern("dd.MM"));

            if (result.containsKey(key)) {
                result.get(key).add(op);
            }
        }

        return result;
    }

    private Map<String, List<OperationDTO>> groupByWeek(List<OperationDTO> operations, LocalDate startDate, LocalDate endDate) {
        Map<String, List<OperationDTO>> result = new LinkedHashMap<>();

        // Инициализируем все недели
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            LocalDate weekStart = current.with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            String key = String.format("%s - %s",
                    weekStart.format(DateTimeFormatter.ofPattern("dd.MM")),
                    weekEnd.format(DateTimeFormatter.ofPattern("dd.MM")));
            result.put(key, new ArrayList<>());
            current = current.plusWeeks(1);
        }

        // Группируем операции по неделям
        for (OperationDTO op : operations) {
            LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate weekStart = opDate.with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            String key = String.format("%s - %s",
                    weekStart.format(DateTimeFormatter.ofPattern("dd.MM")),
                    weekEnd.format(DateTimeFormatter.ofPattern("dd.MM")));

            if (result.containsKey(key)) {
                result.get(key).add(op);
            }
        }

        return result;
    }

    private Map<String, List<OperationDTO>> groupByMonth(List<OperationDTO> operations, LocalDate startDate, LocalDate endDate) {
        Map<String, List<OperationDTO>> result = new LinkedHashMap<>();

        // Инициализируем все месяцы
        LocalDate current = startDate.withDayOfMonth(1);
        LocalDate endMonth = endDate.withDayOfMonth(1);
        while (!current.isAfter(endMonth)) {
            String key = current.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("ru")));
            result.put(key, new ArrayList<>());
            current = current.plusMonths(1);
        }

        // Группируем операции по месяцам
        for (OperationDTO op : operations) {
            LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
            String key = opDate.format(DateTimeFormatter.ofPattern("MMM yyyy", new Locale("ru")));

            if (result.containsKey(key)) {
                result.get(key).add(op);
            }
        }

        return result;
    }

    private List<String> getSortedKeys(Map<String, List<OperationDTO>> groupedData, Period period) {
        List<String> keys = new ArrayList<>(groupedData.keySet());

        switch (period) {
            case DAY:
                // Сортировка по дате и часу
                keys.sort((a, b) -> {
                    try {
                        String dateA = a.split(" ")[0];
                        String dateB = b.split(" ")[0];
                        int compare = compareDates(dateA, dateB);
                        if (compare != 0) return compare;
                        int hourA = Integer.parseInt(a.split(" ")[1].replace(":00", ""));
                        int hourB = Integer.parseInt(b.split(" ")[1].replace(":00", ""));
                        return Integer.compare(hourA, hourB);
                    } catch (Exception e) {
                        return a.compareTo(b);
                    }
                });
                break;
            case WEEK:
            case MONTH:
                // Сортировка по дате начала периода
                keys.sort((a, b) -> {
                    try {
                        String startA = a.split(" - ")[0];
                        String startB = b.split(" - ")[0];
                        return compareDates(startA, startB);
                    } catch (Exception e) {
                        return a.compareTo(b);
                    }
                });
                break;
            case YEAR:
            case ALL:
                // Сортировка по дате (месяцы)
                keys.sort((a, b) -> {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("ru"));
                        LocalDate dateA = LocalDate.parse("01 " + a, DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("ru")));
                        LocalDate dateB = LocalDate.parse("01 " + b, DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("ru")));
                        return dateA.compareTo(dateB);
                    } catch (Exception e) {
                        return a.compareTo(b);
                    }
                });
                break;
        }

        return keys;
    }

    private int compareDates(String dateStr1, String dateStr2) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
            LocalDate date1 = LocalDate.parse(dateStr1 + ".2024", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            LocalDate date2 = LocalDate.parse(dateStr2 + ".2024", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            return date1.compareTo(date2);
        } catch (Exception e) {
            return dateStr1.compareTo(dateStr2);
        }
    }

    private LocalDate getStartDate(Period period) {
        LocalDate now = LocalDate.now();

        switch (period) {
            case DAY:
                return now;
            case WEEK:
                return now.minusWeeks(1);
            case MONTH:
                return now.minusMonths(1);
            case YEAR:
                return now.minusYears(1);
            case ALL:
                return allOperations.stream()
                        .map(op -> op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate())
                        .min(LocalDate::compareTo)
                        .orElse(now);
            default:
                return now.minusMonths(1);
        }
    }

    private List<OperationDTO> filterOperationsByDateRange(LocalDate startDate, LocalDate endDate) {
        return allOperations.stream()
                .filter(op -> {
                    LocalDate opDate = op.getDateTime().atZone(ZoneId.systemDefault()).toLocalDate();
                    return !opDate.isBefore(startDate) && !opDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }

    private void convertToSeries(List<BudgetTimePoint> points, Period period) {
        series.clear();

        if (points.isEmpty()) {
            return;
        }

        XYChart.Series<String, Number> balanceSeries = new XYChart.Series<>();
        balanceSeries.setName("Баланс");

        for (BudgetTimePoint point : points) {
            balanceSeries.getData().add(new XYChart.Data<>(point.getLabel(), point.getBalance()));
        }

        series.add(balanceSeries);
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

        if (currentPeriod.get() != null) {
            refreshDataForPeriod(currentPeriod.get());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class BudgetTimePoint {
        private String label;
        private BigDecimal balance;
        private BigDecimal income;
        private BigDecimal expense;
    }
}