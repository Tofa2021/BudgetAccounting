package org.example.client.controller;

import javafx.fxml.FXML;
import lombok.Getter;
import org.example.client.viewModel.BaseViewModel;

@Getter
public abstract class BaseController<T extends BaseViewModel> {
    protected T viewModel;

    @FXML
    public abstract void initialize();

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
        viewModel.init();
    }

    protected abstract void bindViewModel();
}
