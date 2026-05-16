package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;

@RequiredArgsConstructor
public class NavigationViewModel extends BaseViewModel {
    @Getter
    private final ObjectProperty<Screen> currentScreen = new SimpleObjectProperty<>();
    private final ScreenLoader screenLoader;

    @Override
    public void onViewShown() {
        currentScreen.set(screenLoader.getCurrentScreen());
    }

    public void navigateTo(Screen screen) {
        currentScreen.set(screen);
        screenLoader.load(screen);
    }
}