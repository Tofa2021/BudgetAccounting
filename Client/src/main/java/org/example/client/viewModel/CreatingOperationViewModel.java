package org.example.client.viewModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.client.connection.api.AccountClient;
import org.example.client.connection.api.CategoryClient;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.connection.api.OperationClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.AccountDTO;
import org.example.dto.CategoryDTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class CreatingOperationViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;
    private final OperationClient operationClient;
    private final AccountClient accountClient;
    private final CategoryClient categoryClient;
    private final HouseholdClient householdClient;

    @Getter
    private final StringProperty operationType = new SimpleStringProperty("INCOME");
    @Getter
    private final ObjectProperty<AccountDTO> selectedAccount = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<AccountDTO> accounts = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final ObjectProperty<CategoryDTO> selectedCategory = new SimpleObjectProperty<>();
    @Getter
    private final ListProperty<CategoryDTO> categories = new SimpleListProperty<>(FXCollections.observableArrayList());
    @Getter
    private final DoubleProperty amount = new SimpleDoubleProperty(0);
    @Getter
    private final StringProperty amountText = new SimpleStringProperty("");
    @Getter
    private final StringProperty description = new SimpleStringProperty("");
    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private ObservableList<CategoryDTO> allCategories = FXCollections.observableArrayList();

    @Override
    public void onViewShown() {
        loadAccounts();
        loadCategories();
    }

    private void loadAccounts() {
        Result<List<AccountDTO>> result = accountClient.getMyAccountsInHousehold(sessionContext.getCurrentHousehold().get().getId());
        if (result.isSuccess()) {
            ObservableList<AccountDTO> observableAccounts = FXCollections.observableArrayList(result.getData());
            accounts.set(observableAccounts);
        } else {
            errorMessage.set("Ошибка загрузки счетов: " + result.getErrorMessage());
        }
    }

    private void loadCategories() {
        Result<List<CategoryDTO>> result = categoryClient.getAll(sessionContext.getCurrentHousehold().get().getId());
        if (result.isSuccess()) {
            allCategories = FXCollections.observableArrayList(result.getData());
            putCategoriesInProperty("INCOME");
        } else {
            errorMessage.set("Ошибка загрузки счетов: " + result.getErrorMessage());
        }
    }

    private void putCategoriesInProperty(String type) {
        categories.set(allCategories.filtered(categoryDTO -> categoryDTO.getType().equals(type)));
    }

    public void onIncomeButtonAction() {
        putCategoriesInProperty("INCOME");
    }

    public void onExpenseButtonAction() {
        putCategoriesInProperty("EXPENSE");
    }

    public void createOperation() {
        if (amount.get() <= 0) {
            errorMessage.set("Сумма должна быть больше 0");
            return;
        }

        if (description.get() == null || description.get().trim().isEmpty()) {
            errorMessage.set("Введите описание");
            return;
        }

        if (selectedAccount.get() == null) {
            errorMessage.set("Выберите счет");
            return;
        }

        if (selectedCategory.get() == null) {
            errorMessage.set("Выберите категорию");
            return;
        }

        operationClient.create(selectedAccount.get().getId(), description.get(), BigDecimal.valueOf(amount.get()), selectedCategory.get().getId(), Instant.now());
        screenLoader.load(Screen.OPERATIONS);
        var result = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
        if (result.isSuccess()) {
            sessionContext.getCurrentHousehold().set(result.getData());
        }
    }
}
