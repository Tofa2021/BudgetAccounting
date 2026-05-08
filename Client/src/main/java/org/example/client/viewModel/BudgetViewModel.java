package org.example.client.viewModel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.OperationCategory;

@Getter
public class BudgetViewModel extends BaseViewModel {
    private final DoubleProperty balance = new SimpleDoubleProperty(0);
    private final StringProperty amountInput = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty selectedIncreaseCategory =
            new SimpleStringProperty(OperationCategory.SALARY.getName());
    private final StringProperty selectedDecreaseCategory =
            new SimpleStringProperty(DecreaseOperationCategory.FOOD.getName());


    public BudgetViewModel(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    @Override
    public void init() {
        refreshBalance();
    }

    public void increase(double amount, OperationCategory category) {
        errorMessage.setValue("");
        if (category == null) {
            errorMessage.setValue("Выберите категорию");
            return;
        }

        var result = serverInteractionManager.increaseBudget(amount, category);
        if (result.isSuccess()) {
            refreshBalance();
        }
    }

    public void decrease(double amount, DecreaseOperationCategory category) {
        errorMessage.setValue("");
        if (category == null) {
            errorMessage.setValue("Выберите категорию");
            return;
        }

        var result = serverInteractionManager.decreaseBudget(amount, category);
        if (result.isSuccess()) {
            refreshBalance();
        }
    }

    private void refreshBalance() {
        var result = serverInteractionManager.getAmount();
        if (result.isSuccess()) {
            balance.set(result.getData());
        }
        errorMessage.setValue(result.getErrorMessage());
    }
}
