package org.example.client.screen;

public interface ScreenLoader {
    void load(Screen screen);

    Screen getCurrentScreen();

    void logout();
}
