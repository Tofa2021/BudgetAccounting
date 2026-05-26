package org.example.client.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.Utils;
import org.example.client.connection.api.*;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.*;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OperationViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final OperationClient operationClient;
    private final HouseholdClient householdClient;
    private final HouseholdMemberClient householdMemberClient;
    private final AccountClient accountClient;
    private final CategoryClient categoryClient;
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
    private final ObjectProperty<CategoryDTO> filterCategory = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<CategoryDTO> categories = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final ObjectProperty<AccountDTO> filterAccount = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<AccountDTO> accounts = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final ObjectProperty<HouseholdMemberDTO> filterCreator = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<HouseholdMemberDTO> members = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final ObjectProperty<String> filterOperationType = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<String> operationTypes = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final BooleanProperty filterPanelVisible = new SimpleBooleanProperty();
    @Getter
    private final StringProperty searchText = new SimpleStringProperty();

    @Getter
    private final DoubleProperty filterMinAmount = new SimpleDoubleProperty();
    @Getter
    private final DoubleProperty filterMaxAmount = new SimpleDoubleProperty();

    private List<CategoryDTO> allCategories = new ArrayList<>();
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

        setUpAll();
    }

    private void setUpAll() {
        HouseholdDTO currentHousehold = sessionContext.getCurrentHousehold().get();
        if (currentHousehold == null) {
            showError("Не выбрано домохозяйство");
            return;
        }

        Long currentHouseholdId = currentHousehold.getId();

        refreshOperations();
        setUpCategories(currentHouseholdId);// TODO добавлять в comboBoxes все категории, все счета и тд
        setUpAccounts(currentHouseholdId);
        setUpMembers(currentHouseholdId);
        setUpOperationTypes();
        operations.setAll(allOperations);
    }

    private void setUpOperationTypes() {
        operationTypes.setAll("Все", "Доходы", "Расходы");
        filterOperationType.set("Все");
    }

    private void setUpMembers(Long currentHouseholdId) {
        var result = householdMemberClient.getAllByHouseholdId(currentHouseholdId);
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        members.setAll(result.getData());
        members.addFirst(new HouseholdMemberDTO(null, null, null, null, null, "Все"));
    }

    private void setUpAccounts(Long currentHouseholdId) {
        var result = accountClient.getMyAccountsInHousehold(currentHouseholdId);
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        accounts.setAll(result.getData());
        accounts.addFirst(new AccountDTO(null, "Все", null, null, null, null));
    }

    private void refreshOperations() {
        if (sessionContext.getCurrentHousehold().get() == null) {
            showError("Не выбрано домохозяйство");
            return;
        }

        var result = operationClient.getHouseholdOperations(sessionContext.getCurrentHousehold().get().getId());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        allOperations = result.getData();
    }

    private void setUpCategories(Long currentHouseholdId) {
        var result = categoryClient.getAll(currentHouseholdId);
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }

        allCategories = result.getData();
        categories.setAll(allCategories);
        categories.addFirst(new CategoryDTO(null, "Все", null, null));
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

    public void handleToggleFilterButton() {
        filterPanelVisible.set(!filterPanelVisible.get());
    }

    public void handleCloseFilterButton() {
        filterPanelVisible.set(false);
    }

    public void handleFilter() {
        String operationType = convertOperationType(filterOperationType.get());

        BigDecimal minValue = BigDecimal.valueOf(filterMinAmount.get());
        BigDecimal maxValue = BigDecimal.valueOf(filterMaxAmount.get());
        if (maxValue.compareTo(BigDecimal.ZERO) <= 0) {
            maxValue = allOperations.stream().map(OperationDTO::getAmount).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        }

        CategoryDTO category = filterCategory.get();
        AccountDTO account = filterAccount.get();
        HouseholdMemberDTO member = filterCreator.get();

        BigDecimal finalMaxValue = maxValue;
        List<OperationDTO> operations = allOperations.stream()
                .filter(operation -> operationType == null || operation.getType().equals(operationType))
                .filter(operation -> operation.getAmount().compareTo(minValue) >= 0 && operation.getAmount().compareTo(finalMaxValue) <= 0)
                .filter(operation -> category == null || category.getId() == null || operation.getCategoryId().equals(category.getId()))
                .filter(operation -> account == null || account.getId() == null || operation.getAccountId().equals(account.getId()))
                .filter(operation -> member == null || member.getId() == null || operation.getHouseholdMemberId().equals(member.getId()))
                .toList();

        this.operations.setAll(operations);
    }

    private String convertOperationType(String viewString) {
        return switch (viewString) {
            case "Все" -> null;

            case "Доходы" -> "INCOME";

            case "Расходы" -> "EXPENSE";

            default -> throw new IllegalStateException("Unexpected value: " + viewString);
        };
    }

    public void handleFilterOperationType() {
        switch (filterOperationType.get()) {
            case "Все" -> categories.setAll(allCategories);

            case "Доходы" -> {
                categories.setAll(
                        allCategories.stream()
                                .filter(categoryDTO -> categoryDTO.getType().equals("INCOME"))
                                .toList()
                );
                categories.addFirst(new CategoryDTO(null, "Все", null, null));
            }

            case "Расходы" -> {
                categories.setAll(
                        allCategories.stream()
                                .filter(categoryDTO -> categoryDTO.getType().equals("EXPENSE"))
                                .toList()
                );
                categories.addFirst(new CategoryDTO(null, "Все", null, null));
            }
        }

        handleFilter();
    }
}
