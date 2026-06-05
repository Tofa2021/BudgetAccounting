package org.example.client.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.example.client.viewModel.IncomeExpenseBarChartViewModel;

public class IncomeExpenseBarChartView extends BaseView<IncomeExpenseBarChartViewModel> {
    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private Label placeholder;

    @Override
    public void onViewModelSet() {
        barChart.setData(getViewModel().getSeries());
        placeholder.setVisible(false);

        getViewModel().getSeries().addListener((ListChangeListener<? super XYChart.Series<String, Number>>) c -> {
            placeholder.setVisible(c.getList().isEmpty());
        });
    }
}
