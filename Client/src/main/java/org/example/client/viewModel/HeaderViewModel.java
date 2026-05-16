package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.client.connection.api.AuthClient;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.screen.ScreenLoader;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HeaderViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final HouseholdClient householdClient;
    private final SessionContext sessionContext;
    private final AuthClient authClient;
    @Getter
    private final StringProperty totalAmount = new SimpleStringProperty("0 BYN");
    @Getter
    private final StringProperty userName = new SimpleStringProperty();

    @Override
    public void onViewShown() {
        userName.set(sessionContext.getCurrentUser().getUsername());
        System.err.println(userName.get());
        Result<BigDecimal> amountResult = householdClient.getAmount(sessionContext.getCurrentHousehold().get().getId());
        if (amountResult.isSuccess()) {
            totalAmount.set(amountResult.getData() + " BYN");
        }
        sessionContext.getCurrentHousehold().addListener((obs, old, newHousehold) -> {
            if (newHousehold != null) {
                updateTotalAmount();
            }
        });
    }

    private void updateTotalAmount() {
        if (sessionContext.getCurrentHousehold() != null) {
            Result<BigDecimal> amountResult = householdClient.getAmount(sessionContext.getCurrentHousehold().get().getId());
            if (amountResult.isSuccess() && amountResult.getData() != null) {
                totalAmount.set(String.format("%.2f BYN", amountResult.getData()));
            } else {
                totalAmount.set("0.00 BYN");
            }
        } else {
            totalAmount.set("0.00 BYN");
        }
    }

    public void logout() {
        screenLoader.logout();
        authClient.logout(sessionContext.getRefreshToken());
        sessionContext.setRefreshToken("");
        sessionContext.setAccessToken("");
    }
}