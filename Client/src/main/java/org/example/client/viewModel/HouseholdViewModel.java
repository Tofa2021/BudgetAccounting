package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.HouseholdDTO;

import java.util.Map;

@RequiredArgsConstructor
public class HouseholdViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final SessionContext sessionContext;
    private final HouseholdClient householdClient;

    @Getter
    private final ObservableList<HouseholdDTO> households = FXCollections.observableArrayList();

    @Getter
    private final SimpleStringProperty errorMessage = new SimpleStringProperty();

    @Override
    public void onViewShown() {
        loadHouseholds();
    }

    private void loadHouseholds() {
        var result = householdClient.getMyHouseholds();
        if (result.isSuccess()) {
            households.setAll(result.getData());
            errorMessage.set("");
        } else {
            errorMessage.set("Ошибка загрузки домохозяйств: " + result.getErrorMessage());
        }
    }

    public void selectHousehold(HouseholdDTO household) {
        if (household == null) return;

        sessionContext.getCurrentHousehold().set(household);
        screenLoader.load(Screen.MAIN);
    }

    public void createHousehold(String name) {
        if (name == null || name.trim().isEmpty()) {
            errorMessage.set("Название домохозяйства не может быть пустым");
            return;
        }

        var result = householdClient.create(name.trim(), Map.of());
        if (result.isSuccess()) {
            loadHouseholds();
            errorMessage.set("");
        } else {
            errorMessage.set("Ошибка создания: " + result.getErrorMessage());
        }
    }

    public void handleCreateHouseholdButton() {
        screenLoader.load(Screen.CREATING_HOUSEHOLD);
    }
}