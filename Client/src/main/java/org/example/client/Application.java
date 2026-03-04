package org.example.client;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseBudgetCategory;

public class Application extends javafx.application.Application {
    private final RRManager rrManager = new RRManager();
    private final IntegerProperty balanceProperty = new SimpleIntegerProperty(0);

    public Application() {
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

        ComboBox<DecreaseOperationCategory> decreseCategoryComboBox = new ComboBox<>();
        decreseCategoryComboBox.getItems().addAll(
                DecreaseOperationCategory.values()
        );
        decreseCategoryComboBox.setValue(DecreaseOperationCategory.FOOD);
        decreseCategoryComboBox.setPromptText("Выберите категорию");
        decreseCategoryComboBox.setPrefWidth(150);

        ComboBox<IncreaseBudgetCategory> increseCategoryComboBox = new ComboBox<>();
        increseCategoryComboBox.getItems().addAll(
                IncreaseBudgetCategory.values()
        );
        increseCategoryComboBox.setValue(IncreaseBudgetCategory.SALARY);
        increseCategoryComboBox.setPromptText("Выберите категорию");
        increseCategoryComboBox.setPrefWidth(150);

        HBox categoryBox = new HBox(20, increseCategoryComboBox, decreseCategoryComboBox);

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
            increaseBalance(Integer.parseInt(amountField.getText()), increseCategoryComboBox.getValue());
        });
        decreaseButton.setOnAction(e -> {
            decreaseBalance(Integer.parseInt(amountField.getText()), decreseCategoryComboBox.getValue());
        });

        HBox inputBox = new HBox(10, amountLabel, amountField);
        inputBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox(20, increaseButton, decreaseButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(balanceLabel, inputBox, buttonBox, categoryBox);

        Scene scene = new Scene(root, 500, 250);
        stage.setScene(scene);
        stage.show();
    }

    private void loadInitialBalance() {
        int amount = rrManager.getAmount();
        balanceProperty.set(amount);
    }

    private void increaseBalance(int amount, IncreaseBudgetCategory category) {
        rrManager.increaseBudget(amount, category);
        refreshBalance();
    }

    private void decreaseBalance(int amount, DecreaseOperationCategory category) {
        rrManager.decreaseBudget(amount, category);
        refreshBalance();
    }

    private void refreshBalance() {
        int amount = rrManager.getAmount();
        balanceProperty.set(amount);
    }
}