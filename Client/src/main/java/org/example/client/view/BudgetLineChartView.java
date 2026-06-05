package org.example.client.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.client.viewModel.BudgetLineChartViewModel;

public class BudgetLineChartView extends BaseView<BudgetLineChartViewModel> {
    @FXML
    private LineChart<String, Number> lineChart;

    @FXML
    private VBox periodSelectorParent;

    @FXML
    private Label placeholder;

    private PeriodSelectorView periodSelector;

    @Override
    public void onViewModelSet() {
        lineChart.setData(getViewModel().getSeries());

        getViewModel().getSeries().addListener((ListChangeListener<? super XYChart.Series<String, Number>>) c -> {
            placeholder.setVisible(c.getList().isEmpty());
        });
    }

    public void setPeriodSelector(PeriodSelectorView periodSelector, Parent selectorParent) {
        this.periodSelector = periodSelector;
        periodSelectorParent.getChildren().add(selectorParent);
        periodSelector.getViewModel().getSelectedPeriod().addListener((observable, oldValue, newValue) -> {
            getViewModel().getCurrentPeriod().set(newValue);
        });
    }
}
