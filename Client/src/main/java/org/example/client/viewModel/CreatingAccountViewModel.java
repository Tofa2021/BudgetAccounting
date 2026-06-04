package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.AccountClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.HouseholdDTO;
import org.example.enums.Currency;

@RequiredArgsConstructor
public class CreatingAccountViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;
    private final AccountClient accountClient;

    @Getter
    private final StringProperty nameProperty = new SimpleStringProperty();
    @Getter
    private final ObjectProperty<Currency> selectedCurrency = new SimpleObjectProperty<>();
    @Getter
    private final ObservableList<Currency> currencies = FXCollections.observableArrayList();

    @Override
    public void onViewShown() {
        currencies.setAll(Currency.values());
    }

    public void handleCreateButton() {
        HouseholdDTO currentHousehold = sessionContext.getCurrentHousehold().get();
        if (currentHousehold == null) {
            showError("Нет домохозяйства");
            return;
        }

        String name = nameProperty.get();
        if (name == null || name.isBlank()) {
            showError("Поле название счета не может быть пустым");
            return;
        }

        Currency currency = selectedCurrency.get();
        if (currency == null) {
            showError("Поле валюта обязательное");
            return;
        }

        var result = accountClient.create(name, currency, currentHousehold.getId());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return;
        }
        
        showInfo("Счет «" + name + "» создан");
        screenLoader.load(Screen.ACCOUNTS);
    }
}
