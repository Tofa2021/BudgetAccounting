package org.example.client.viewModel;

import javafx.beans.property.*;
import lombok.Getter;
import org.example.client.RRManager;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

@Getter
public class BudgetViewModel extends BaseViewModel {
    private final IntegerProperty balance = new SimpleIntegerProperty(0);
    private final StringProperty amountInput = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final ObjectProperty<IncreaseOperationCategory> selectedIncreaseCategory =
            new SimpleObjectProperty<>();
    private final ObjectProperty<DecreaseOperationCategory> selectedDecreaseCategory =
            new SimpleObjectProperty<>();


    public BudgetViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
        refreshBalance();
    }

    public void increase(int amount, IncreaseOperationCategory category) {
        var result = rrManager.increaseBudget(amount, category);
        if (result.isSuccess()) {
            refreshBalance();
        }
    }

    public void decrease(int amount, DecreaseOperationCategory category) {
        var result = rrManager.decreaseBudget(amount, category);
        if (result.isSuccess()) {
            refreshBalance();
        }
    }

    private void refreshBalance() {
        var result = rrManager.getAmount();
        if (result.isSuccess()) {
            balance.set(result.getData());
        }
        statusMessage.setValue(result.getErrorMessage());
    }
}
