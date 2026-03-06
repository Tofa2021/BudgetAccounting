package org.example.client.viewModel;

import javafx.beans.property.*;
import lombok.Getter;
import org.example.client.ApplicationContext;
import org.example.client.RRManager;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

@Getter
public class BudgetViewModel {
    private final RRManager rrManager = ApplicationContext.getInstance().getRrManager();

    private final IntegerProperty balance = new SimpleIntegerProperty(0);
    private final StringProperty amountInput = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final ObjectProperty<IncreaseOperationCategory> selectedIncreaseCategory =
            new SimpleObjectProperty<>();
    private final ObjectProperty<DecreaseOperationCategory> selectedDecreaseCategory =
            new SimpleObjectProperty<>();

    public BudgetViewModel() {
        rrManager.signin("Anton", "1234");
        refreshBalance();
    }

    public void increase(int amount, IncreaseOperationCategory category) {
        try {
            rrManager.increaseBudget(amount, category);
            refreshBalance();
            statusMessage.set("Доход добавлен");
        } catch (Exception e) {
            statusMessage.set("Ошибка: " + e.getMessage());
        }
    }

    public void decrease(int amount, DecreaseOperationCategory category) {
        try {
            rrManager.decreaseBudget(amount, category);
            refreshBalance();
            statusMessage.set("Расход добавлен");
        } catch (Exception e) {
            statusMessage.set("Ошибка: " + e.getMessage());
        }
    }

    private void refreshBalance() {
        balance.set(rrManager.getAmount());
    }
}
