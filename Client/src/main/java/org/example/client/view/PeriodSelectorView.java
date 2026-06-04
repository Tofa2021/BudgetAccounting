package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.client.viewModel.Period;
import org.example.client.viewModel.PeriodSelectorViewModel;

public class PeriodSelectorView extends BaseView<PeriodSelectorViewModel> {
    private static final String SELECTED_STYLE = "selected-operation-button";
    @FXML
    private Button dayButton;
    @FXML
    private Button weekButton;
    @FXML
    private Button monthButton;
    @FXML
    private Button yearButton;
    @FXML
    private Button allButton;

    @Override
    public void onViewModelSet() {
        getViewModel().getSelectedPeriod().addListener((observable, oldValue, newValue) -> {
            updateSelectedPeriod(oldValue, newValue);
        });
    }

    private void updateSelectedPeriod(Period oldPeriod, Period newPeriod) {
        if (oldPeriod != null) {
            unselectPeriodButton(oldPeriod);
        }
        selectPeriodButton(newPeriod);
    }

    private void unselectPeriodButton(Period period) {
        switch (period) {
            case DAY -> dayButton.getStyleClass().remove(SELECTED_STYLE);

            case WEEK -> weekButton.getStyleClass().remove(SELECTED_STYLE);

            case MONTH -> monthButton.getStyleClass().remove(SELECTED_STYLE);

            case YEAR -> yearButton.getStyleClass().remove(SELECTED_STYLE);

            case ALL -> allButton.getStyleClass().remove(SELECTED_STYLE);
        }
    }

    private void selectPeriodButton(Period period) {
        switch (period) {
            case DAY -> dayButton.getStyleClass().add(SELECTED_STYLE);

            case WEEK -> weekButton.getStyleClass().add(SELECTED_STYLE);

            case MONTH -> monthButton.getStyleClass().add(SELECTED_STYLE);

            case YEAR -> yearButton.getStyleClass().add(SELECTED_STYLE);

            case ALL -> allButton.getStyleClass().add(SELECTED_STYLE);
        }
    }

    @FXML
    private void handleDayButton() {
        getViewModel().handleDayButton();
    }

    @FXML
    private void handleWeekButton() {
        getViewModel().handleWeekButton();
    }

    @FXML
    private void handleMonthButton() {
        getViewModel().handleMonthButton();
    }

    @FXML
    private void handleYearButton() {
        getViewModel().handleYearButton();
    }

    @FXML
    private void handleAllButton() {
        getViewModel().handleAllButton();
    }
}
