package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.ApplicationContext;
import org.example.client.RRManager;

@Getter
public class AuthViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final RRManager rrManager = ApplicationContext.getInstance().getRrManager();

    public void signIn(String username, String password) {
        rrManager.signin(username, password);
    }
}
