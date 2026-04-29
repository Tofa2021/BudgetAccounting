package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.connection.ServerInteractionManager;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

@Getter
public class RegistrationViewModel extends BaseViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorInput = new SimpleStringProperty("");

    public RegistrationViewModel(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    @Override
    public void init() {
    }

    public boolean signUp(String username, String password) {
        var result = serverInteractionManager.signUp(username, password);
        if (result.isSuccess()) {
            SceneManager.getInstance().loadScene(Scene.BUDGET);
            return true;
        }
        errorInput.setValue(result.getErrorMessage());
        return false;
    }
}
