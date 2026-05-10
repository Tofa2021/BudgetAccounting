package org.example.client.viewModel;

import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.CachedClientAPIFactory;
import org.example.client.screen.ScreenLoader;

@RequiredArgsConstructor
public class ViewModelFactory {
    private final CachedClientAPIFactory clientAPIFactory;
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;

    public AuthViewModel createAuthViewModel() {
        return new AuthViewModel(clientAPIFactory.createAuthClient(), clientAPIFactory.createUserClient(), screenLoader, sessionContext);
    }

    public RegistrationViewModel createRegistrationViewModel() {
        return new RegistrationViewModel(clientAPIFactory.createAuthClient(), clientAPIFactory.createUserClient(), screenLoader, sessionContext);
    }

    public OperationViewModel createOperationViewModel() {
        return new OperationViewModel(clientAPIFactory.createOperationClient(), sessionContext);
    }
}
