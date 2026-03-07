package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.RRManager;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

@Getter
public class RegistrationViewModel extends BaseViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorInput = new SimpleStringProperty("");

    public RegistrationViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
    }

    public void signUp(String username, String password) {
        var result = rrManager.signUp(username, password);
        if (result.isSuccess()) {
            SceneManager.getInstance().loadScene(Scene.BUDGET);
        }
        errorInput.setValue(result.getErrorMessage());

    }
}
