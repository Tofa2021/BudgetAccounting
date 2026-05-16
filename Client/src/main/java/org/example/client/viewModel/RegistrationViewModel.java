package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.Pair;
import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.client.connection.api.AuthClient;
import org.example.client.connection.api.UserClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.UserDTO;

@Getter
@RequiredArgsConstructor
public class RegistrationViewModel extends BaseViewModel {
    private final AuthClient authClient;
    private final UserClient userClient;
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;

    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty confirmationPasswordInput = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Override
    public void onViewShown() {
        usernameInput.set("");
        passwordInput.set("");
        errorMessage.set("");
    }

    public void signUp() {
        String username = usernameInput.get();
        String password = passwordInput.get();

        if (!isIncorrectInput(username, password)) {
            errorMessage.set("Поле не может быть пустым");
            return;
        }

        String confirmationPassword = confirmationPasswordInput.get();
        if (!confirmationPassword.equals(password)) {
            errorMessage.set("Пароли должны совпадать");
            return;
        }

        Result<Pair<String, String>> result = authClient.signUp(username, password);
        if (!result.isSuccess()) {
            errorMessage.setValue(result.getErrorMessage());
            return;
        }

        Pair<String, String> tokens = result.getData();
        sessionContext.setAccessToken(tokens.getFirst());
        sessionContext.setRefreshToken(tokens.getSecond());
        sessionContext.setCurrentUser(getMe());

        screenLoader.load(Screen.OPERATIONS);
    }

    private UserDTO getMe() {
        Result<UserDTO> result = userClient.getMe();
        if (!result.isSuccess()) {
            errorMessage.set(result.getErrorMessage());
            return null;
        }

        return result.getData();
    }

    private boolean isIncorrectInput(String username, String password) {
        return !username.isBlank() && !password.isBlank();
    }

    public void linkSignIn() {
        screenLoader.load(Screen.AUTH);
    }
}
