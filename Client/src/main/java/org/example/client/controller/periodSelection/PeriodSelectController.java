package org.example.client.controller.periodSelection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Setter;

@Setter
public class PeriodSelectController {
    private final String DEFAULT_BUTTON_STYLE =
            "-fx-background-color: white; " +
                    "-fx-text-fill: #2c3e50; " +
                    "-fx-border-color: #dddddd; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 20; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 8 15 8 15; " +
                    "-fx-font-size: 13px; " +
                    "-fx-cursor: hand;";
    private final String SELECTED_BUTTON_STYLE =
            "-fx-background-color: #007bff; " +
                    "-fx-text-fill: white; " +
                    "-fx-border-color: #dddddd; " +
                    "-fx-border-width: 1; " +
                    "-fx-border-radius: 20; " +
                    "-fx-background-radius: 20; " +
                    "-fx-padding: 8 15 8 15; " +
                    "-fx-font-size: 13px; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,123,255,0.3), 8, 0, 0, 2);";
    @FXML
    private ToggleButton weekButton;
    @FXML
    private ToggleButton monthButton;
    @FXML
    private ToggleButton threeMonthsButton;
    @FXML
    private ToggleButton halfYearButton;
    @FXML
    private ToggleButton yearButton;
    @FXML
    private ToggleButton allButton;
    private ToggleGroup periodToggleGroup;
    private PeriodSelectionListener listener;

    @FXML
    public void initialize() {
        setupPeriodSelector();
    }

    private void setupPeriodSelector() {
        periodToggleGroup = new ToggleGroup();

        weekButton.setToggleGroup(periodToggleGroup);
        monthButton.setToggleGroup(periodToggleGroup);
        threeMonthsButton.setToggleGroup(periodToggleGroup);
        halfYearButton.setToggleGroup(periodToggleGroup);
        yearButton.setToggleGroup(periodToggleGroup);
        allButton.setToggleGroup(periodToggleGroup);

        periodToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                resetButtonStyles();
                ToggleButton selectedButton = (ToggleButton) newToggle;
                selectedButton.setStyle(SELECTED_BUTTON_STYLE);

                if (listener != null) {
                    notifyListener(selectedButton);
                }
            }
        });

        weekButton.setSelected(true);
    }

    public void resetButtonStyles() {
        weekButton.setStyle(DEFAULT_BUTTON_STYLE);
        monthButton.setStyle(DEFAULT_BUTTON_STYLE);
        threeMonthsButton.setStyle(DEFAULT_BUTTON_STYLE);
        halfYearButton.setStyle(DEFAULT_BUTTON_STYLE);
        yearButton.setStyle(DEFAULT_BUTTON_STYLE);
        allButton.setStyle(DEFAULT_BUTTON_STYLE);
    }

    @FXML
    private void handlePeriodClick(ActionEvent event) {
        ToggleButton source = (ToggleButton) event.getSource();
        source.setSelected(true);
    }

    public void restorePeriod() {
        ToggleButton toggleButton = (ToggleButton) periodToggleGroup.getSelectedToggle();
        toggleButton.setStyle(SELECTED_BUTTON_STYLE);
        notifyListener(toggleButton);
    }

    private void notifyListener(ToggleButton selectedButton) {
        if (listener == null) {
            return;
        }

        if (selectedButton == weekButton) {
            listener.onPeriodSelected(7);
        } else if (selectedButton == monthButton) {
            listener.onPeriodSelected(30);
        } else if (selectedButton == threeMonthsButton) {
            listener.onPeriodSelected(90);
        } else if (selectedButton == halfYearButton) {
            listener.onPeriodSelected(180);
        } else if (selectedButton == yearButton) {
            listener.onPeriodSelected(365);
        } else if (selectedButton == allButton) {
            listener.onAllTimeSelected();
        }
    }
}
