package org.example.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Setter;

@Setter
public class PeriodSelectController {
    private final String DEFAULT_BUTTON_STYLE = "-fx-background-color: #e0e0e0; -fx-text-fill: black;";
    private final String SELECTED_BUTTON_STYLE = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;";
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
