package org.example.client;

import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dto.OperationCategory;

public class HelloApplication extends Application {
    private final RRManager rrManager = new RRManager();
    private final IntegerProperty balanceProperty = new SimpleIntegerProperty(0);

    public HelloApplication() {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        loadInitialBalance();

        stage.setTitle("BudgetAccounting");

        Label balanceLabel = new Label();
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        balanceLabel.textProperty().bind(
                balanceProperty.asString("Текущий баланс: $%d")
        );

        TextField amountField = new TextField();
        amountField.setPromptText("Введите сумму");
        amountField.setMaxWidth(150);

        Label amountLabel = new Label("Сумма:");

        Button increaseButton = new Button("Увеличить баланс");
        Button decreaseButton = new Button("Уменьшить баланс");
        increaseButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px;");
        decreaseButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        increaseButton.setPrefWidth(150);
        decreaseButton.setPrefWidth(150);

        increaseButton.setOnAction(e -> {
            increaseBalance(Integer.parseInt(amountField.getText()));
        });
        decreaseButton.setOnAction(e -> {
            decreaseBalance(Integer.parseInt(amountField.getText()));
        });

        HBox inputBox = new HBox(10, amountLabel, amountField);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(20, increaseButton, decreaseButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(balanceLabel, inputBox, buttonBox);

        Scene scene = new Scene(root, 500, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void loadInitialBalance() {
        int amount = rrManager.getAmount();
        balanceProperty.set(amount);
    }

    private void increaseBalance(int amount) {
        rrManager.increaseBudget(amount, OperationCategory.FOOD);
        refreshBalance();
    }

    private void decreaseBalance(int amount) {
        rrManager.decreaseBudget(amount, OperationCategory.TRANSPORT);
        refreshBalance();
    }

    private void refreshBalance() {
        int amount = rrManager.getAmount();
        balanceProperty.set(amount);
    }
}