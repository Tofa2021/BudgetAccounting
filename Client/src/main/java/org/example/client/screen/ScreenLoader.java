package org.example.client.screen;

import javafx.beans.property.ObjectProperty;

public interface ScreenLoader {
    void load(Screen screen);

    ObjectProperty<Screen> getCurrentScreen();
}
