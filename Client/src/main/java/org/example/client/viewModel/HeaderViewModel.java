package org.example.client.viewModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.client.Utils;
import org.example.client.connection.api.AuthClient;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class HeaderViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;
    private final AuthClient authClient;
    private final HouseholdClient householdClient;
    @Getter
    private final ObservableList<String> currencyAmounts = FXCollections.observableArrayList();
    @Getter
    private final StringProperty selectedCurrencyAmount = new SimpleStringProperty();
    @Getter
    private final StringProperty username = new SimpleStringProperty("Войти");
    @Getter
    private final BooleanProperty profileButtonDisabled = new SimpleBooleanProperty(true);
    @Getter
    private final BooleanProperty currencyAmountComboBoxVisible = new SimpleBooleanProperty(false);

    @Override
    public void onViewShown() {
        sessionContext.getCurrentHousehold().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        updateCurrencyAmounts();
                    }
                }
        );

        sessionContext.getCurrentUser().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        setAuthorizedState(newValue.getUsername());
                    }
                }
        );
    }

    private void updateCurrencyAmounts() {
        selectedCurrencyAmount.set("");
        currencyAmounts.clear();

        if (sessionContext.getCurrentHousehold().get() == null) {
            currencyAmounts.add("Нет домохозяйства");
            selectedCurrencyAmount.set("Нет домохозяйства");
            return;
        }

        Result<Map<Currency, BigDecimal>> amountResult = householdClient.getAmount(sessionContext.getCurrentHousehold().get().getId());
        if (amountResult.isSuccess()) {
            Map<Currency, BigDecimal> amounts = amountResult.getData();
            if (amounts.isEmpty()) {
                currencyAmounts.add("0");
                selectedCurrencyAmount.set("0");
                return;
            }

            List<String> strings = amounts.entrySet()
                    .stream()
                    .map(entry -> convertCurrencyAmount(entry.getKey(), entry.getValue()))
                    .toList();
            currencyAmounts.setAll(strings);
            selectedCurrencyAmount.set(strings.getFirst());
        }
        showError(amountResult.getErrorMessage());
    }

    private String convertCurrencyAmount(Currency currency, BigDecimal amount) {
        String amountString = Utils.convertBigDecimalToString(amount);
        return String.format("%s %s", amountString, currency.getCode());
    }

    public void logout() {
        authClient.logout(sessionContext.getRefreshToken().get());
        setUnauthorizedState();
        screenLoader.load(Screen.AUTH);
        sessionContext.clear();
    }

    private void setUnauthorizedState() {
        currencyAmountComboBoxVisible.set(false);
        profileButtonDisabled.set(true);
        currencyAmounts.clear();
        username.set("Войти");
    }

    private void setAuthorizedState(String username) {
        currencyAmountComboBoxVisible.set(true);
        profileButtonDisabled.set(false);
        updateCurrencyAmounts();
        this.username.set(username);
    }
}