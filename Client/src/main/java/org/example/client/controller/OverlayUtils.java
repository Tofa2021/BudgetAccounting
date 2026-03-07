package org.example.client.controller;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OverlayUtils {
    public static void showOverlay(Stage ownerStage, String title, VBox content) {
        Rectangle dim = new Rectangle(ownerStage.getWidth(), ownerStage.getHeight(),
                Color.rgb(0, 0, 0, 0.5));

        StackPane overlayPane = new StackPane();
        overlayPane.getChildren().addAll(dim, content);

        Scene overlayScene = new Scene(overlayPane);
        overlayScene.setFill(Color.TRANSPARENT);

        Stage overlayStage = new Stage();
        overlayStage.initOwner(ownerStage);
        overlayStage.initModality(Modality.WINDOW_MODAL);
        overlayStage.initStyle(StageStyle.TRANSPARENT);
        overlayStage.setScene(overlayScene);

        ownerStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            dim.setWidth(newVal.doubleValue());
            overlayStage.setWidth(newVal.doubleValue());
        });

        ownerStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            dim.setHeight(newVal.doubleValue());
            overlayStage.setHeight(newVal.doubleValue());
        });

        overlayStage.setWidth(ownerStage.getWidth());
        overlayStage.setHeight(ownerStage.getHeight());

        overlayStage.show();
    }
}
