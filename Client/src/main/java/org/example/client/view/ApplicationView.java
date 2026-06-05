package org.example.client.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.viewModel.ApplicationViewModel;
import org.example.client.viewModel.BaseViewModel;

import java.util.LinkedList;
import java.util.Queue;

@RequiredArgsConstructor
public class ApplicationView implements FXMLLoadable {
    private static final int MAX_VISIBLE_NOTIFICATIONS = 5;
    private final Queue<NotificationItem> notificationQueue = new LinkedList<>();
    private final ApplicationViewModel viewModel;
    private boolean isShowing = false;
    @FXML
    @Getter
    private BorderPane mainRoot;
    @FXML
    private VBox notificationRoot;
    private Parent navigationParent;

    private void bindNavigation() {
        navigationParent.visibleProperty().bind(viewModel.getNavigationVisible());
        navigationParent.managedProperty().bind(viewModel.getNavigationManaged());
    }

    public <T extends BaseViewModel> void bindViewModel(T viewModel) {
        if (viewModel == null) return;

        viewModel.getErrorMessage().addListener((obs, old, message) -> {
            if (message != null && !message.isEmpty()) {
                showError(message);
                viewModel.clearMessages();
            }
        });

        viewModel.getSuccessMessage().addListener((obs, old, message) -> {
            if (message != null && !message.isEmpty()) {
                showSuccess(message);
                viewModel.clearMessages();
            }
        });

        viewModel.getInfoMessage().addListener((obs, old, message) -> {
            if (message != null && !message.isEmpty()) {
                showInfo(message);
                viewModel.clearMessages();
            }
        });
    }

    public void showError(String message) {
        addToQueue(message, NotificationType.ERROR);
    }

    public void showInfo(String message) {
        addToQueue(message, NotificationType.INFO);
    }

    public void showSuccess(String message) {
        addToQueue(message, NotificationType.SUCCESS);
    }

    private void addToQueue(String message, NotificationType type) {
        if (message == null || message.isEmpty()) return;
        notificationQueue.add(new NotificationItem(message, type));
        processQueue();
    }

    private void processQueue() {
        if (isShowing || notificationQueue.isEmpty()) return;

        if (notificationRoot.getChildren().size() >= MAX_VISIBLE_NOTIFICATIONS) {
            removeOldestNotification();
            return;
        }

        isShowing = true;
        NotificationItem notification = notificationQueue.poll();
        showNotification(notification);
    }

    private void showNotification(NotificationItem notification) {
        Platform.runLater(() -> {
            HBox toast = createNotificationToast(notification);

            notificationRoot.getChildren().add(toast);

            toast.setTranslateX(400);
            toast.setOpacity(0);

            Timeline showAnim = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(toast.translateXProperty(), 400),
                            new KeyValue(toast.opacityProperty(), 0)),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(toast.translateXProperty(), 0),
                            new KeyValue(toast.opacityProperty(), 1))
            );
            showAnim.play();

            PauseTransition autoHide = new PauseTransition(Duration.seconds(3));
            autoHide.setOnFinished(e -> hideNotification(toast));
            autoHide.play();
        });
    }

    private void hideNotification(HBox toast) {
        Timeline hideAnim = new Timeline(
                new KeyFrame(Duration.millis(300),
                        new KeyValue(toast.translateXProperty(), 400),
                        new KeyValue(toast.opacityProperty(), 0))
        );
        hideAnim.setOnFinished(ev -> {
            notificationRoot.getChildren().remove(toast);
            isShowing = false;
            processQueue();
        });
        hideAnim.play();
    }

    private void removeOldestNotification() {
        if (!notificationRoot.getChildren().isEmpty()) {
            HBox oldest = (HBox) notificationRoot.getChildren().getFirst();
            hideNotification(oldest);
        }
    }

    private HBox createNotificationToast(NotificationItem notification) {
        HBox toast = new HBox(12);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setPadding(new Insets(12, 20, 12, 20));
        toast.setMaxWidth(350);
        toast.setMinWidth(250);
        toast.setStyle("-fx-background-radius: 8;");

        String color;
        String icon = switch (notification.type) {
            case ERROR -> {
                color = "#f44336";
                yield "❌";
            }
            case SUCCESS -> {
                color = "#4caf50";
                yield "✅";
            }
            case INFO -> {
                color = "#2196f3";
                yield "ℹ️";
            }
            default -> {
                color = "#666666";
                yield "📌";
            }
        };

        toast.setStyle(toast.getStyle() +
                "-fx-background-color: " + color + ";" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 2, 2);" +
                "-fx-border-color: rgba(255,255,255,0.2);" +
                "-fx-border-radius: 8;"
        );

        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        Label textLabel = new Label(notification.message);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(280);

        Label closeButton = new Label("✕");
        closeButton.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 0 0 10;"
        );
        closeButton.setOnMouseClicked(e -> hideNotification(toast));

        toast.getChildren().addAll(iconLabel, textLabel, closeButton);

        toast.setOnMouseEntered(e -> {
            toast.setScaleX(1.02);
            toast.setScaleY(1.02);
        });
        toast.setOnMouseExited(e -> {
            toast.setScaleX(1.0);
            toast.setScaleY(1.0);
        });

        return toast;
    }


    public void setNavigation(Parent navigation) {
        mainRoot.setLeft(navigation);
        navigationParent = navigation;
        bindNavigation();
    }

    public void setHeader(Parent header) {
        mainRoot.setTop(header);
    }

    public void setContent(Parent content) {
        mainRoot.setCenter(content);
    }

    public void show() {
        viewModel.onViewShown();
    }
    
    private enum NotificationType {
        ERROR,
        SUCCESS,
        INFO,
    }

    private record NotificationItem(String message, NotificationType type) {
    }
}
