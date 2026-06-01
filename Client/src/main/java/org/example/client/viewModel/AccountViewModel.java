package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.AccountClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.AccountDTO;
import org.example.dto.HouseholdDTO;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AccountViewModel extends BaseViewModel {
    @Getter
    private final ObservableList<AccountDTO> accounts = FXCollections.observableArrayList();
    private final List<AccountDTO> allAccounts = new ArrayList<>();
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;
    private final AccountClient accountClient;

    @Override
    public void onViewShown() {
        refreshAccounts();
        accounts.setAll(allAccounts);
    }

    private void refreshAccounts() {
        HouseholdDTO currentHousehold = sessionContext.getCurrentHousehold().get();
        if (currentHousehold == null) {
            return;
        }
        Long householdId = currentHousehold.getId();

        var result = accountClient.getMyAccountsInHousehold(householdId);
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        allAccounts.clear();
        allAccounts.addAll(result.getData());
    }

    public void handleCreateAccountButton() {
        screenLoader.load(Screen.CREATING_ACCOUNT);
    }
}
