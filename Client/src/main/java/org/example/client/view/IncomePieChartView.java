package org.example.client.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.example.client.viewModel.IncomePieChartViewModel;

public class IncomePieChartView extends BaseView<IncomePieChartViewModel> {
    @FXML
    private PieChart pieChart;

    @FXML
    private VBox periodSelectorParent;

    @FXML
    private Label placeholder;

    private PeriodSelectorView periodSelector;

    @Override
    public void onViewModelSet() {
        pieChart.setData(getViewModel().getIncomePieData());
        placeholder.setVisible(false);

        getViewModel().getIncomePieData().addListener((ListChangeListener<? super PieChart.Data>) c -> {
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
