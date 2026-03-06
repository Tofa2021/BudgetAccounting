package org.example.client;

import lombok.Getter;

@Getter
public class ApplicationContext {
    private static ApplicationContext instance;
    private final RRManager rrManager = new RRManager();

    private ApplicationContext() {
    }

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }
}
