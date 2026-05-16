package org.example.client.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.client.model.*;
import org.example.client.viewModel.MainViewModel;

import java.util.List;

public class MainView extends BaseView<MainViewModel> {

    @FXML
    private Label incomeAmount;
    @FXML
    private Label expenseAmount;
    @FXML
    private VBox incomeCategoriesContainer;
    @FXML
    private VBox expenseCategoriesContainer;
    @FXML
    private VBox trendsContainer;
    @FXML
    private VBox membersContainer;
    @FXML
    private Button inviteButton;
    @FXML
    private VBox operationsContainer;
    @FXML
    private VBox limitsContainer;

    @Override
    protected void onViewModelSet() {
        incomeAmount.textProperty().bind(viewModel.incomeAmountProperty());
        expenseAmount.textProperty().bind(viewModel.expenseAmountProperty());

        viewModel.getIncomeCategories().addListener((ListChangeListener<CategoryItem>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasReplaced()) {
                    updateIncomeCategories(viewModel.getIncomeCategories());
                }
            }
        });

        viewModel.getIncomeCategories().addListener((ListChangeListener<CategoryItem>) change -> {
            updateIncomeCategories(viewModel.getIncomeCategories());
        });

        viewModel.getExpenseCategories().addListener((ListChangeListener<CategoryItem>) change -> {
            updateExpenseCategories(viewModel.getExpenseCategories());
        });

        viewModel.getTrends().addListener((ListChangeListener<TrendItem>) change -> {
            updateTrends(viewModel.getTrends());
        });

        viewModel.getMembers().addListener((ListChangeListener<MemberItem>) change -> {
            updateMembers(viewModel.getMembers());
        });

        viewModel.getOperations().addListener((ListChangeListener<OperationItem>) change -> {
            updateOperations(viewModel.getOperations());
        });

        viewModel.getLimits().addListener((ListChangeListener<LimitItem>) change -> {
            updateLimits(viewModel.getLimits());
        });

        updateIncomeCategories(viewModel.getIncomeCategories());
        updateExpenseCategories(viewModel.getExpenseCategories());
        updateTrends(viewModel.getTrends());
        updateMembers(viewModel.getMembers());
        updateOperations(viewModel.getOperations());
        updateLimits(viewModel.getLimits());

        setupEventHandlers();
        viewModel.loadData();
    }

    private void setupEventHandlers() {
        inviteButton.setOnAction(e -> viewModel.onInviteClicked());
    }

    private void updateIncomeCategories(List<CategoryItem> categories) {
        incomeCategoriesContainer.getChildren().clear();
        for (CategoryItem cat : categories) {
            incomeCategoriesContainer.getChildren().add(createCategoryRow(cat));
        }
    }

    private void updateExpenseCategories(List<CategoryItem> categories) {
        expenseCategoriesContainer.getChildren().clear();
        for (CategoryItem cat : categories) {
            expenseCategoriesContainer.getChildren().add(createCategoryRow(cat));
        }
    }

    private void updateTrends(List<TrendItem> trends) {
        trendsContainer.getChildren().clear();
        for (TrendItem trend : trends) {
            Label trendLabel = new Label(trend.text());
            trendLabel.getStyleClass().add("trend-item");
            trendsContainer.getChildren().add(trendLabel);
        }
    }

    private void updateMembers(List<MemberItem> members) {
        membersContainer.getChildren().clear();
        for (MemberItem member : members) {
            membersContainer.getChildren().add(createMemberRow(member));
        }
    }

    private void updateOperations(List<OperationItem> operations) {
        operationsContainer.getChildren().clear();
        operationsContainer.getChildren().add(createOperationsTable(operations));
    }

    private void updateLimits(List<LimitItem> limits) {
        limitsContainer.getChildren().clear();
        for (LimitItem limit : limits) {
            limitsContainer.getChildren().add(createLimitRow(limit));
        }
    }

    // Helper methods to create UI components dynamically
    private HBox createCategoryRow(CategoryItem cat) {
        HBox row = new HBox(10);
        row.getStyleClass().add("category-item");
        // Add icon, name, amount, percent labels
        return row;
    }

    private HBox createMemberRow(MemberItem member) {
        HBox row = new HBox();
        row.getStyleClass().add("member-item");
        // Add name, role labels and edit icon
        return row;
    }

    private GridPane createOperationsTable(List<OperationItem> operations) {
        GridPane table = new GridPane();
        table.getStyleClass().add("operations-table");
        // Create table header and rows
        return table;
    }

    private VBox createLimitRow(LimitItem limit) {
        VBox row = new VBox(6);
        row.getStyleClass().add("limit-item");
        // Add limit name, progress bar, and label
        return row;
    }
}