package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.Utils;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.connection.api.OperationClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.OperationDTO;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OperationViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final OperationClient operationClient;
    private final HouseholdClient householdClient;
    private final SessionContext sessionContext;
    @Getter
    private final ObservableList<OperationDTO> operations = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<String> incomes = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<String> expenses = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<String> profits = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<String> amountOperations = FXCollections.observableArrayList();
    private Map<Currency, List<OperationDTO>> groupedByCurrency = new HashMap<>();

    @Override
    public void onViewShown() {
        operations.addListener((ListChangeListener<? super OperationDTO>) change -> {
            boolean needRecalc = false;
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasReplaced()) {
                    needRecalc = true;
                }
            }
            if (needRecalc) {
                processOperations();
            }
        });
        refreshOperations();
    }

    public void refreshOperations() {
        if (sessionContext.getCurrentHousehold().get() == null) {
            return;
        }

        var result = operationClient.getHouseholdOperations(sessionContext.getCurrentHousehold().get().getId());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        List<OperationDTO> operations = result.getData();
        groupedByCurrency = operations.stream()
                .collect(Collectors.groupingBy(OperationDTO::getCurrency));
        this.operations.setAll(operations.stream()
                .sorted(Comparator.comparing(OperationDTO::getDateTime))
                .toList()
                .reversed());
    }

    private void processOperations() {
        List<String> incomeStrings = new ArrayList<>();
        List<String> expenseStrings = new ArrayList<>();
        List<String> profitStrings = new ArrayList<>();
        List<String> amountOperationsStrings = new ArrayList<>();
        for (Map.Entry<Currency, List<OperationDTO>> entry : groupedByCurrency.entrySet()) {
            BigDecimal income = getIncome(entry.getValue());
            incomeStrings.add(Utils.convertBigDecimalToString(income, "INCOME", entry.getKey()));

            BigDecimal expense = getExpense(entry.getValue());
            expenseStrings.add(Utils.convertBigDecimalToString(expense, "EXPENSE", entry.getKey()));

            profitStrings.add(Utils.convertBigDecimalToString(income.subtract(expense), entry.getKey()));

            amountOperationsStrings.add(Utils.convertIntToString(entry.getValue().size()));
        }

        incomes.setAll(incomeStrings);
        expenses.setAll(expenseStrings);
        profits.setAll(profitStrings);
        amountOperations.setAll(amountOperationsStrings);
    }

    private BigDecimal getIncome(List<OperationDTO> operations) {
        return operations.stream()
                .filter(operation -> operation.getType().equals("INCOME"))
                .map(OperationDTO::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getExpense(List<OperationDTO> operations) {
        return operations.stream()
                .filter(operation -> operation.getType().equals("EXPENSE"))
                .map(OperationDTO::getAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public void delete(OperationDTO operationDTO) {
        var result = operationClient.delete(operationDTO.getId());
        if (result.isSuccess()) {
            operations.remove(operationDTO);

            var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
            if (householdDTOResult.isSuccess()) {
                sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
            }
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

            var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
            if (householdDTOResult.isSuccess()) {
                sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
            }
        }
    }

    public void handleCreatOperationButton() {
        screenLoader.load(Screen.CREATING_OPERATION);
    }
}
