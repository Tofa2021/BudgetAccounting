package org.example.client.view;

import lombok.Getter;
import lombok.Setter;
import org.example.client.viewModel.BaseViewModel;

@Getter
public abstract class BaseView<T extends BaseViewModel> implements FXMLLoadable {
    @Setter
    private ApplicationView applicationView;
    private T viewModel;

    public void show() {
        viewModel.onViewShown();
    }

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
        this.applicationView.bindViewModel(viewModel);
        onViewModelSet();
    }

    public abstract void onViewModelSet();

    public void showError(String message) {
        if (applicationView != null) {
            applicationView.showError(message);
        }
    }

    public void showSuccess(String message) {
        if (applicationView != null) {
            applicationView.showSuccess(message);
        }
    }

    public void showInfo(String message) {
        if (applicationView != null) {
            applicationView.showInfo(message);
        }
    }
}
