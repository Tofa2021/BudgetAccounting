package org.example.client.controller.overlay;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public class UpdateOperationOverlay {
    private final OperationDTO operation;
    private final Runnable onSaveCallback;
    private final NumberFormat numberFormat;
    private Stage overlayStage;

    public UpdateOperationOverlay(Stage ownerStage, OperationDTO operation, Runnable onSaveCallback) {
        this.operation = operation;
        this.onSaveCallback = onSaveCallback;
        this.numberFormat = NumberFormat.getInstance(Locale.getDefault());
        createOverlay(ownerStage);
    }

    private void createOverlay(Stage ownerStage) {
        Rectangle dim = new Rectangle(ownerStage.getWidth(), ownerStage.getHeight(),
                Color.rgb(0, 0, 0, 0.7));

        VBox form = createUpdateForm();
        form.setMaxWidth(400);
        form.setMaxHeight(300);

        StackPane overlayPane = new StackPane();
        overlayPane.getChildren().addAll(dim, form);

        Scene overlayScene = new Scene(overlayPane);
        overlayScene.setFill(Color.TRANSPARENT);

        overlayStage = new Stage();
        overlayStage.initOwner(ownerStage);
        overlayStage.initModality(Modality.WINDOW_MODAL);
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        overlayStage.setScene(overlayScene);

        overlayStage.setWidth(ownerStage.getWidth());
        overlayStage.setHeight(ownerStage.getHeight());

        ownerStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            dim.setWidth(newVal.doubleValue());
            overlayStage.setWidth(newVal.doubleValue());
        });

        ownerStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            dim.setHeight(newVal.doubleValue());
            overlayStage.setHeight(newVal.doubleValue());
        });
    }

    private VBox createUpdateForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(20));
        form.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);"
        );

        Label titleLabel = new Label("Редактирование операции");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField amountField = new TextField(String.format("%.2f", operation.getAmount()).replace(',', '.'));
        amountField.setPromptText("Сумма");

        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                amountField.setText(oldValue);
            }
        });

        ComboBox<String> categoryCombo = new ComboBox<>();
        if (operation instanceof IncreaseOperationDTO) {
            categoryCombo.getItems().addAll(Arrays.stream(IncreaseOperationCategory.values())
                    .map(IncreaseOperationCategory::getName)
                    .toList());
            categoryCombo.valueProperty().setValue(((IncreaseOperationDTO) operation).getCategory().getName());
        } else if (operation instanceof DecreaseOperationDTO) {
            categoryCombo.getItems().addAll(Arrays.stream(DecreaseOperationCategory.values())
                    .map(DecreaseOperationCategory::getName)
                    .toList());
            categoryCombo.valueProperty().setValue(((DecreaseOperationDTO) operation).getCategory().getName());
        }
        categoryCombo.setPromptText("Категория");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Сохранить");
        saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        saveButton.setPrefWidth(100);

        Button cancelButton = new Button("Отмена");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelButton.setPrefWidth(100);

        buttonBox.getChildren().addAll(saveButton, cancelButton);

        form.getChildren().addAll(titleLabel, amountField, categoryCombo, buttonBox);

        saveButton.setOnAction(e -> {
            double newAmount = Double.parseDouble(amountField.getText().replace(',', '.'));
            String newCategoryName = categoryCombo.getValue();
            if (operation instanceof IncreaseOperationDTO increaseOperationDTO) {
                increaseOperationDTO.setAmount(newAmount);
                IncreaseOperationCategory newCategory = Arrays.stream(IncreaseOperationCategory.values())
                        .filter(cat -> cat.getName().equals(newCategoryName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                increaseOperationDTO.setCategory(newCategory);
            } else if (operation instanceof DecreaseOperationDTO decreaseOperationDTO) {
                decreaseOperationDTO.setAmount(newAmount);
                DecreaseOperationCategory newCategory = Arrays.stream(DecreaseOperationCategory.values())
                        .filter(cat -> cat.getName().equals(newCategoryName))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Category not found"));
                decreaseOperationDTO.setCategory(newCategory);
            }

            onSaveCallback.run();
            close();
        });

        cancelButton.setOnAction(e -> close());

        return form;
    }

    public void show() {
        overlayStage.show();
    }

    public void close() {
        overlayStage.close();
    }
}