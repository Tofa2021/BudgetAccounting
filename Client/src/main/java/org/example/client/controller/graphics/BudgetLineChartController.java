package org.example.client.controller.graphics;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BudgetLineChartController {
    private final List<LocalDate> datePoints = new ArrayList<>();
    private final List<XYChart.Data<Number, Number>> dataPoints = new ArrayList<>();
    private double currentBudget;

    @FXML
    private LineChart<Number, Number> lineChart;

    private XYChart.Series<Number, Number> balanceSeries;
    private ObservableList<OperationDTO> operations;

    @FXML
    public void initialize() {
        setupChart();
    }

    public void bindItems(ObservableList<OperationDTO> items) {
        this.operations = items;
        this.operations.addListener((ListChangeListener<OperationDTO>) change -> {
            System.out.println("Operations changed new size: " + change.getList().size());
            updateChart();
        });
        updateChart();
    }

    public void setCurrentBudget(double budget) {
        this.currentBudget = budget;
        updateChart();
    }

    private void setupChart() {
        lineChart.setCreateSymbols(true);
        lineChart.setTitle("Динамика бюджета");

        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLabel("Дата");
        xAxis.setAutoRanging(true);
        xAxis.setTickLabelFormatter(new javafx.util.StringConverter<Number>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(Number number) {
                int index = number.intValue();
                if (index >= 0 && index < datePoints.size()) {
                    return datePoints.get(index).format(formatter);
                }
                return "";
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLabel("Сумма (руб)");
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);

        balanceSeries = new XYChart.Series<>();
        balanceSeries.setName("Бюджет");

        lineChart.getData().add(balanceSeries);
    }

    private void updateChart() {
        balanceSeries.getData().clear();
        datePoints.clear();
        dataPoints.clear();

        if (operations == null || operations.isEmpty()) {
            if (currentBudget > 0) {
                LocalDate today = LocalDate.now();
                datePoints.add(today);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(0, currentBudget);
                balanceSeries.getData().add(dataPoint);
                dataPoints.add(dataPoint);

                javafx.application.Platform.runLater(() -> {
                    addTooltipToDataPoint(dataPoint, today, currentBudget);
                });
            }
            return;
        }

        List<OperationDTO> sortedOperations = new ArrayList<>(operations);
        sortedOperations.sort(Comparator.comparing(OperationDTO::getDateTime));

        Map<LocalDate, Double> dailyChanges = new TreeMap<>();
        for (OperationDTO op : sortedOperations) {
            LocalDate date = op.getDateTime()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            double change = 0;
            if (op instanceof IncreaseOperationDTO) {
                change = op.getAmount();
            } else if (op instanceof DecreaseOperationDTO) {
                change = -op.getAmount();
            }

            dailyChanges.merge(date, change, Double::sum);
        }

        double runningBudget = currentBudget;
        for (Map.Entry<LocalDate, Double> entry : dailyChanges.entrySet()) {
            runningBudget -= entry.getValue();
        }

        int pointIndex = 0;

        LocalDate firstDate = dailyChanges.keySet().iterator().next();
        datePoints.add(firstDate.minusDays(1));
        XYChart.Data<Number, Number> firstPoint = new XYChart.Data<>(pointIndex, runningBudget);
        balanceSeries.getData().add(firstPoint);
        dataPoints.add(firstPoint);
        pointIndex++;

        for (Map.Entry<LocalDate, Double> entry : dailyChanges.entrySet()) {
            LocalDate date = entry.getKey();
            double change = entry.getValue();

            datePoints.add(date);
            XYChart.Data<Number, Number> beforePoint = new XYChart.Data<>(pointIndex, runningBudget);
            balanceSeries.getData().add(beforePoint);
            dataPoints.add(beforePoint);
            pointIndex++;

            runningBudget += change;

            datePoints.add(date);
            XYChart.Data<Number, Number> afterPoint = new XYChart.Data<>(pointIndex, runningBudget);
            balanceSeries.getData().add(afterPoint);
            dataPoints.add(afterPoint);
            pointIndex++;
        }

        LocalDate lastDate = dailyChanges.keySet().stream().reduce((first, second) -> second).get();
        datePoints.add(lastDate.plusDays(1));
        XYChart.Data<Number, Number> lastPoint = new XYChart.Data<>(pointIndex, runningBudget);
        balanceSeries.getData().add(lastPoint);
        dataPoints.add(lastPoint);

        lineChart.setTitle(String.format("Динамика бюджета (текущий: %.2f руб)", currentBudget));

        javafx.application.Platform.runLater(this::addTooltipsToAllPoints);
    }

    private void addTooltipsToAllPoints() {
        for (int i = 0; i < dataPoints.size(); i++) {
            XYChart.Data<Number, Number> dataPoint = dataPoints.get(i);
            LocalDate date = datePoints.get(i);
            double budget = dataPoint.getYValue().doubleValue();

            addTooltipToDataPoint(dataPoint, date, budget);
        }
    }

    private void addTooltipToDataPoint(XYChart.Data<Number, Number> dataPoint, LocalDate date, double budget) {
        javafx.scene.Node node = dataPoint.getNode();

        if (node != null) {
            installTooltip(node, date, budget);
        } else {
            dataPoint.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    installTooltip(newNode, date, budget);
                }
            });
        }
    }

    private void installTooltip(javafx.scene.Node node, LocalDate date, double budget) {
        String tooltipText = String.format("Дата: %s\nБюджет: %.2f руб",
                date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),
                budget);

        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle(
                "-fx-font-size: 12px; " +
                        "-fx-background-color: #333333; " +
                        "-fx-text-fill: white; " +
                        "-fx-padding: 5px; " +
                        "-fx-background-radius: 3px;"
        );

        Tooltip.install(node, tooltip);

        node.setOnMouseEntered(e -> {
            node.setStyle(
                    "-fx-background-color: red; " +
                            "-fx-background-radius: 5px; " +
                            "-fx-scale-x: 1.5; " +
                            "-fx-scale-y: 1.5;"
            );
        });

        node.setOnMouseExited(e -> {
            node.setStyle("");
        });
    }
}