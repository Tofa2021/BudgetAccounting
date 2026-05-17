package org.example.client.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.example.client.viewModel.HouseholdViewModel;
import org.example.dto.HouseholdDTO;

public class HouseholdView extends BaseView<HouseholdViewModel> {
    @FXML
    public ListView<HouseholdDTO> listView;
    @FXML
    public Button createHouseholdButton;

    @Override
    protected void onViewModelSet() {
        setupListView();
        bindHouseholds();
    }

    private void setupListView() {
        listView.setCellFactory(lv -> new HouseholdCell());

        listView.setPlaceholder(new Label("Нет домохозяйств\nНажмите 'Создать домохозяйство' чтобы добавить"));

        setupSelectionListener();
    }

    private void setupSelectionListener() {
        listView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        viewModel.selectHousehold(newValue);
                    }
                }
        );
    }

    private void bindHouseholds() {
        listView.setItems(viewModel.getHouseholds());
    }

    @FXML
    private void handleCreateHouseholdButton() {
        viewModel.handleCreateHouseholdButton();
    }

    private static class HouseholdCell extends ListCell<HouseholdDTO> {
        private final VBox container = new VBox();
        private final Label nameLabel = new Label();
        private final Label statsLabel = new Label();

        public HouseholdCell() {
            container.setPadding(new Insets(12, 15, 12, 15));
            container.setSpacing(5);
            container.getStyleClass().add("operation-card"); // переиспользуем стиль карточки

            nameLabel.getStyleClass().add("h4");
            statsLabel.getStyleClass().add("operation-category");

            container.getChildren().addAll(nameLabel, statsLabel);
        }

        @Override
        protected void updateItem(HouseholdDTO household, boolean empty) {
            super.updateItem(household, empty);

            if (empty || household == null) {
                setGraphic(null);
                return;
            }

            nameLabel.setText(household.getName());

            int membersCount = household.getMemberIds() != null ? household.getMemberIds().size() : 0;
            int accountsCount = household.getAccountIds() != null ? household.getAccountIds().size() : 0;
            int categoriesCount = household.getCategoryIds() != null ? household.getCategoryIds().size() : 0;

            statsLabel.setText(String.format("%d участников | %d счетов | %d категорий",
                    membersCount, accountsCount, categoriesCount));

            setGraphic(container);
        }
    }
}