package org.example.client.viewModel;

import lombok.AllArgsConstructor;
import org.example.client.RRManager;

@AllArgsConstructor
public abstract class BaseViewModel {
    protected final RRManager rrManager;

    public abstract void init();
}
