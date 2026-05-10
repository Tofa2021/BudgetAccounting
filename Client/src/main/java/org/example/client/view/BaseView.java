package org.example.client.view;

import org.example.client.viewModel.BaseViewModel;

public abstract class BaseView<T extends BaseViewModel> {
    protected T viewModel;

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
        onViewModelSet();
    }

    protected abstract void onViewModelSet();
}
