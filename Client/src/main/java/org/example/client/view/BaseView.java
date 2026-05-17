package org.example.client.view;

import lombok.Setter;
import org.example.client.viewModel.BaseViewModel;

public abstract class BaseView<T extends BaseViewModel> implements FXMLLoadable {
    protected T viewModel;
    @Setter
    protected ApplicationView applicationView;

    public void show() {
        viewModel.onViewShown();
    }

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
        applicationView.bindViewModel(viewModel);
        onViewModelSet();
    }

    protected abstract void onViewModelSet();

    protected void showError(String message) {
        if (applicationView != null) {
            applicationView.showError(message);
        }
    }

    protected void showSuccess(String message) {
        if (applicationView != null) {
            applicationView.showSuccess(message);
        }
    }

    protected void showInfo(String message) {
        if (applicationView != null) {
            applicationView.showInfo(message);
        }
    }
}
