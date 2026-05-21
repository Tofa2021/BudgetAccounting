package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.example.client.viewModel.CreatingHouseholdViewModel;

public class CreatingHouseholdView extends BaseView<CreatingHouseholdViewModel> {
    @FXML
    private Button createButton;
    @FXML
    private TextField nameTextField;

    @Override
    public void onViewModelSet() {
        nameTextField.textProperty().bindBidirectional(getViewModel().getHouseholdName());
    }

    @FXML
    private void handleCreateButton() {
        getViewModel().handleCreateButton();
    }
}
