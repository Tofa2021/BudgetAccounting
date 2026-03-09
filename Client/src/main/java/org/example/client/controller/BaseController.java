package org.example.client.controller;

import lombok.Getter;
import org.example.client.viewModel.BaseViewModel;

@Getter
public abstract class BaseController<T extends BaseViewModel> {
    protected T viewModel;

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        viewModel.init();
    }

    protected abstract void bindViewModel();
}
