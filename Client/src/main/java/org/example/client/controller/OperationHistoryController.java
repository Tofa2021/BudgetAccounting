package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.client.controller.filter.FilterListener;
import org.example.client.controller.filter.OperationFilterController;
import org.example.client.controller.overlay.UpdateOperationOverlay;
import org.example.client.controller.periodSelection.PeriodSelectController;
import org.example.client.controller.periodSelection.PeriodSelectionListener;
import org.example.client.controller.table.OperationActionListener;
import org.example.client.controller.table.OperationTableController;
import org.example.client.viewModel.OperationHistoryViewModel;
import org.example.dto.model.OperationDTO;

import java.time.Instant;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel>
        implements PeriodSelectionListener, FilterListener, OperationActionListener {
    @FXML
    private VBox root;

    @FXML
    private VBox operationTableVBox;
    @FXML
    private OperationTableController operationTableVBoxController;

    @FXML
    private VBox operationFilterVBox;
    @FXML
    private OperationFilterController operationFilterVBoxController;

    @FXML
    private VBox periodSelectVBox;
    @FXML
    private PeriodSelectController periodSelectVBoxController;

    @FXML
    public void initialize() {
        setupTable();
        setupPeriodSelector();
        setupFilter();
    }

    @Override
    protected void bindViewModel() {
        operationTableVBoxController.bindItems(viewModel.getOperations());
        viewModel.loadRecentOperations(7);
    }

    private void setupPeriodSelector() {
        periodSelectVBoxController.setListener(this);
    }

    private void setupFilter() {
        operationFilterVBoxController.setListener(this);
    }

    private void setupTable() {
        operationTableVBoxController.setListener(this);
    }

    @Override
    public void onPeriodSelected(int days) {
        viewModel.loadRecentOperations(days);
        operationFilterVBoxController.clearFilters();
        operationFilterVBoxController.setExpanded(false);
    }

    @Override
    public void onAllTimeSelected() {
        viewModel.refreshOperations();
        operationFilterVBoxController.clearFilters();
        operationFilterVBoxController.setExpanded(false);
    }

    @Override
    public void onFiltersCleared() {
        periodSelectVBoxController.restorePeriod();
    }

    @Override
    public void onFiltersApplied(String type, String category, Instant dateFrom, Instant dateTo, Integer minAmount, Integer maxAmount) {
        viewModel.getFilteredOperations(type, category, dateFrom, dateTo, minAmount, maxAmount);
        periodSelectVBoxController.resetButtonStyles();
    }

    @Override
    public void onOperationUpdated(OperationDTO operationDTO) {
        Stage currentStage = (Stage) root.getScene().getWindow();
        UpdateOperationOverlay overlay = new UpdateOperationOverlay(
                currentStage,
                operationDTO,
                () -> viewModel.update(operationDTO)
        );
        overlay.show();
    }

    @Override
    public void onOperationDeleted(OperationDTO operationDTO) {
        viewModel.delete(operationDTO);
    }
}
