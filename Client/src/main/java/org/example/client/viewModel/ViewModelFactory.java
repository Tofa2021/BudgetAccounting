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
        return new AuthViewModel(clientAPIFactory.createAuthClient(), clientAPIFactory.createUserClient(), clientAPIFactory.createHouseholdClient(), screenLoader, sessionContext);
    }

    public RegistrationViewModel createRegistrationViewModel() {
        return new RegistrationViewModel(clientAPIFactory.createAuthClient(), clientAPIFactory.createUserClient(), screenLoader, sessionContext);
    }

    public OperationViewModel createOperationViewModel() {
        return new OperationViewModel(screenLoader, clientAPIFactory.createOperationClient(), clientAPIFactory.createHouseholdClient(), sessionContext);
    }

    public NavigationViewModel createNavigationViewModel() {
        return new NavigationViewModel(screenLoader);
    }

    public HeaderViewModel createHeaderViewModel() {
        return new HeaderViewModel(screenLoader, sessionContext, clientAPIFactory.createAuthClient(), clientAPIFactory.createHouseholdClient());
    }

    public CreatingOperationViewModel createCreatingOperationViewModel() {
        return new CreatingOperationViewModel(
                screenLoader,
                sessionContext,
                clientAPIFactory.createOperationClient(),
                clientAPIFactory.createAccount(),
                clientAPIFactory.createCategory(),
                clientAPIFactory.createHouseholdClient()
        );
    }

    public HouseholdViewModel createHouseholdViewModel() {
        return new HouseholdViewModel(screenLoader, sessionContext, clientAPIFactory.createHouseholdClient());
    }

    public ApplicationViewModel createApplicationViewModel() {
        return new ApplicationViewModel(screenLoader);
    }

    public CreatingHouseholdViewModel createCreatingHouseholdViewModel() {
        return new CreatingHouseholdViewModel(screenLoader, clientAPIFactory.createHouseholdClient());
    }
}
