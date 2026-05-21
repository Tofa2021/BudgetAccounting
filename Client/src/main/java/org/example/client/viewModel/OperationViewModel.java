package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Getter
    private final StringProperty searchText = new SimpleStringProperty();
    private List<OperationDTO> allOperations = new ArrayList<>();

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
                updateAdditionalInformation();
            }
        });

        refreshOperations();
        operations.setAll(allOperations);
    }

    public void refreshOperations() {
        if (sessionContext.getCurrentHousehold().get() == null) {
            return;
        }

        var result = operationClient.getHouseholdOperations(sessionContext.getCurrentHousehold().get().getId());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        allOperations = result.getData();
    }

    private void updateAdditionalInformation() {
        Map<Currency, List<OperationDTO>> groupedByCurrency = operations.stream()
                .collect(Collectors.groupingBy(OperationDTO::getCurrency));

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
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        allOperations.remove(operationDTO);
        operations.remove(operationDTO);

        //TODO update household amount
        var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
        if (householdDTOResult.isSuccess()) {
            sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
        }
    }

    public void update(OperationDTO operationDTO) {
        var result = operationClient.update(operationDTO);
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        operations.stream()
                .filter(operationDTO1 -> Objects.equals(operationDTO1.getId(), operationDTO.getId()))
                .findFirst()
                .ifPresent(oldOperation -> {
                    int index = operations.indexOf(oldOperation);
                    operations.set(index, operationDTO);
                });

        //TODO update household amount
        var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
        if (householdDTOResult.isSuccess()) {
            sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
        }
    }

    public void handleCreateOperationButton() {
        screenLoader.load(Screen.CREATING_OPERATION);
    }

    public void searchOperations() {
        String search = searchText.get().toLowerCase();

        List<OperationDTO> searchedOperations = allOperations.stream()
                .filter(operationDTO -> operationDTO.getDescription().toLowerCase().contains(search))
                .toList();

        operations.setAll(searchedOperations);
    }
}
