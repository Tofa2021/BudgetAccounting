package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.RRManager;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

@Getter
public class AuthViewModel extends BaseViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public AuthViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
    }

    public void signIn(String username, String password) {
        if (!isValidatedInput()) {
            return;
        }

        var result = rrManager.signIn(username, password);
        if (result.isSuccess()) {
            SceneManager.getInstance().loadScene(Scene.BUDGET);
        }
        errorMessage.setValue(result.getErrorMessage());

    }

    private boolean isValidatedInput() {
        String usernameValue = usernameInput.getValue();
        String passwordValue = passwordInput.getValue();

        if (usernameValue.isEmpty()) {
            errorMessage.setValue("Логин не может быть пустым");
            return false;
        }

        if (passwordValue.isEmpty()) {
            errorMessage.setValue("Пароль не может быть пустым");
            return false;
        }

        return true;
    }
}
