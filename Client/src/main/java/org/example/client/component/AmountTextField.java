package org.example.client.component;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.example.client.Utils;

public class AmountTextField extends TextField {
    private static final String AMOUNT_PATTERN = "\\d*[\\.\\,]?\\d{0,2}";
    private static final String ERROR_STYLE = "-fx-border-color: #f44336; -fx-border-width: 2px;";
    private static final String DEFAULT_STYLE = "";

    private final DoubleProperty amount = new SimpleDoubleProperty(0);
    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty();
    @Getter
    private final ReadOnlyBooleanWrapper valid = new ReadOnlyBooleanWrapper(true);

    public AmountTextField() {
        initialize();
    }

    public AmountTextField(String promptText) {
        setPromptText(promptText);
        initialize();
    }

    private void initialize() {
        setupListener();
        setupStyleClass();
    }

    private void setupStyleClass() {
        getStyleClass().add("amount-text-field");
    }

    private void setupListener() {
        textProperty().addListener((obs, oldValue, newValue) -> {
            handleInput(oldValue, newValue);
        });


    }

    private void handleInput(String oldValue, String newValue) {
        if (newValue != null && !newValue.isEmpty()) {
            if (newValue.matches(AMOUNT_PATTERN)) {
                String normalizedValue = newValue.replace(",", ".");
                try {
                    double parsedAmount = Double.parseDouble(normalizedValue);
                    if (parsedAmount > 0) {
                        setValidAmount(parsedAmount);
                    } else {
                        setInvalidAmount("Сумма должна быть больше 0");
                    }
                } catch (NumberFormatException e) {
                    setInvalidAmount("Введите корректную сумму");
                }
            } else {
                Platform.runLater(() -> {
                    setText(oldValue);
                    getParent().requestFocus();
                    getOnAction().handle(new ActionEvent());
                });
            }
        } else {
            clearAmount();
        }
    }

    private void setValidAmount(double value) {
        amount.set(value);
        clearErrorStyle();
        errorMessage.set(null);
        valid.set(true);
    }

    private void setInvalidAmount(String message) {
        amount.set(0);
        setErrorStyle();
        errorMessage.set(message);
        valid.set(false);
    }

    private void clearAmount() {
        amount.set(0);
        clearErrorStyle();
        errorMessage.set(null);
        valid.set(true);
    }

    private void setErrorStyle() {
        setStyle(ERROR_STYLE);
    }

    private void clearErrorStyle() {
        setStyle(DEFAULT_STYLE);
    }

    public void clear() {
        setText("");
        amount.set(0);
        clearErrorStyle();
        errorMessage.set(null);
        valid.set(true);
    }

    public void setAmount(double value) {
        if (value > 0) {
            setText(Utils.convertDoubleToString(value));
            amount.set(value);
            clearErrorStyle();
            errorMessage.set(null);
            valid.set(true);
        } else {
            clear();
        }
    }

    public DoubleProperty amountProperty() {
        return amount;
    }
}