package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.RRManager;

@Getter
public class AuthViewModel extends BaseViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");

    public AuthViewModel(RRManager rrManager) {
        super(rrManager);
    }

    @Override
    public void init() {
    }

    public void signIn(String username, String password) {
        rrManager.signin(username, password);
    }
}
