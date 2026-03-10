package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.example.client.controller.graphics.BudgetLineChartController;
import org.example.client.controller.periodSelection.PeriodSelectController;
import org.example.client.controller.periodSelection.PeriodSelectionListener;
import org.example.client.viewModel.OperationHistoryViewModel;

public class GraphicsController extends BaseController<OperationHistoryViewModel> implements PeriodSelectionListener {
    @FXML
    private VBox budgetLineChartVBox;
    @FXML
    private BudgetLineChartController budgetLineChartVBoxController;

    @FXML
    private VBox periodSelectVBox;
    @FXML
    private PeriodSelectController periodSelectVBoxController;

    @Override
    protected void bindViewModel() {
        periodSelectVBoxController.setListener(this);
        budgetLineChartVBoxController.bindItems(viewModel.getOperations());
        viewModel.loadRecentOperations(7);
        budgetLineChartVBoxController.setCurrentBudget(viewModel.getBalance());
    }

    @Override
    public void onPeriodSelected(int days) {
        viewModel.loadRecentOperations(days);
    }

    @Override
    public void onAllTimeSelected() {
        viewModel.refreshOperations();
    }
}
