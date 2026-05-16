package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.Pair;
import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.client.connection.api.AuthClient;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.connection.api.UserClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.HouseholdDTO;
import org.example.dto.UserDTO;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AuthViewModel extends BaseViewModel {
    private final AuthClient authClient;
    private final UserClient userClient;
    private final HouseholdClient householdClient;
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;

    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Override
    public void onViewShown() {
        usernameInput.set("");
        passwordInput.set("");
        errorMessage.set("");
    }

    public void signIn() {
        String username = usernameInput.get();
        String password = passwordInput.get();

        if (!isInputCorrect(username, password)) {
            errorMessage.set("Поле не может быть пустым");
            return;
        }

        Result<Pair<String, String>> result = authClient.signIn(username, password);
        if (!result.isSuccess()) {
            errorMessage.set(result.getErrorMessage());
            return;
        }

        Pair<String, String> tokens = result.getData();
        sessionContext.setAccessToken(tokens.getFirst());
        sessionContext.setRefreshToken(tokens.getSecond());
        sessionContext.setCurrentUser(getMe());
        sessionContext.getCurrentHousehold().set(getMyHousehold());

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

    private HouseholdDTO getMyHousehold() {
        Result<List<HouseholdDTO>> result = householdClient.getMyHouseholds();
        if (!result.isSuccess()) {
            errorMessage.set(result.getErrorMessage());
            return null;
        }

        return result.getData().getFirst();
    }

    public void linkSignUp() {
        screenLoader.load(Screen.REGISTRATION);
    }

    private boolean isInputCorrect(String username, String password) {
        return !username.isBlank() && !password.isBlank();
    }
}

