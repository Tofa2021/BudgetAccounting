package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;

import java.util.Map;

@RequiredArgsConstructor
public class CreatingHouseholdViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final HouseholdClient householdClient;
    @Getter
    private final StringProperty householdName = new SimpleStringProperty();

    @Override
    public void onViewShown() {
        householdName.set("");
    }

    public void handleCreateButton() {
        String name = householdName.get();

        if (name == null) {
            showError("Введите название домохозяйства");
        }

        var result = householdClient.create(name, Map.of());
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
        }

        screenLoader.load(Screen.HOUSEHOLDS);
    }
}
