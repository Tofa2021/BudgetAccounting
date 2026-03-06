package org.example.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.client.controller.BudgetController;

import java.io.IOException;

public class Application extends javafx.application.Application {
    public Application() {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/client/budget-view.fxml")
        );

        Scene scene = loader.load();

        BudgetViewModel budgetViewModel = new BudgetViewModel();
        BudgetController budgetController = loader.getController();
        budgetController.setBudgetViewModel(budgetViewModel);

        stage.setTitle("BudgetAccounting");
        stage.setScene(scene);
        stage.show();
    }
}