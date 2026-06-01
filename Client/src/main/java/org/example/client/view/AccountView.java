package org.example.client.view;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.example.client.Utils;
import org.example.client.viewModel.AccountViewModel;
import org.example.dto.AccountDTO;

public class AccountView extends BaseView<AccountViewModel> {
    @FXML
    private ListView<AccountDTO> listView;

    @FXML
    private Button createAccountButton;

    @Override
    public void onViewModelSet() {
        listView.setItems(getViewModel().getAccounts());
        setupListView();
    }

    private void setupListView() {
        listView.setCellFactory(lv -> new AccountView.AccountCell());

        listView.setPlaceholder(new Label("Нет счетов\nНажмите 'Создать счет' чтобы добавить"));
    }

    @FXML
    public void handleCreateAccountButton() {
        getViewModel().handleCreateAccountButton();
    }

    private static class AccountCell extends ListCell<AccountDTO> {
        private final VBox container = new VBox();
        private final Label nameLabel = new Label();
        private final Label statsLabel = new Label();

        public AccountCell() {
            container.setPadding(new Insets(12, 15, 12, 15));
            container.setSpacing(5);
            container.getStyleClass().add("operation-card");

            nameLabel.getStyleClass().add("h4");
            statsLabel.getStyleClass().add("operation-category");

            container.getChildren().addAll(nameLabel, statsLabel);
        }

        @Override
        protected void updateItem(AccountDTO account, boolean empty) {
            super.updateItem(account, empty);

            if (empty || account == null) {
                setGraphic(null);
                return;
            }

            nameLabel.setText(account.getName());

            /*
             * account:
             *      name
             *      members
             *      amount
             *      currency
             *
             * */

            nameLabel.setText(account.getName());
            int membersCount = account.getMemberIds() != null ? account.getMemberIds().size() : 0;
            String amountWithCurrency = Utils.convertBigDecimalToString(account.getAmount(), account.getCurrency());

            statsLabel.setText(String.format("%d участников | %s сумма",
                    membersCount, amountWithCurrency));

            setGraphic(container);
        }
    }
}
