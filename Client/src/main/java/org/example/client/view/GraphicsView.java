package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.client.viewModel.GraphicsViewModel;

public class GraphicsView extends BaseView<GraphicsViewModel> {
    @FXML
    private VBox expensePieChartParent;

    @FXML
    private VBox incomePieChartParent;

    @FXML
    private VBox lineChartParent;

    @FXML
    private VBox barParent;

    @Override
    public void onViewModelSet() {

    }

    public void setExpensePieChart(Parent chartParent) {
        expensePieChartParent.getChildren().add(chartParent);
    }

    public void setIncomePieChart(Parent chartParent) {
        incomePieChartParent.getChildren().add(chartParent);
    }
}
