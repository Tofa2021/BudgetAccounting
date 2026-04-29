package org.example.client.viewModel;

import lombok.AllArgsConstructor;
import org.example.client.connection.ServerInteractionManager;

@AllArgsConstructor
public abstract class BaseViewModel {
    protected final ServerInteractionManager serverInteractionManager;

    public abstract void init();
}
