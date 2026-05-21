package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.client.Result;

@AllArgsConstructor
public abstract class BaseViewModel {
    @Getter
    private final StringProperty errorMessage = new SimpleStringProperty();
    @Getter
    private final StringProperty successMessage = new SimpleStringProperty();
    @Getter
    private final StringProperty infoMessage = new SimpleStringProperty();

    public abstract void onViewShown();

    public void showError(String message) {
        errorMessage.set(message);
    }

    public void showSuccess(String message) {
        successMessage.set(message);
    }

    public void showInfo(String message) {
        infoMessage.set(message);
    }

    public void clearMessages() {
        errorMessage.set(null);
        successMessage.set(null);
        infoMessage.set(null);
    }

    public <R> R uncoverResult(Result<R> result) {
        if (!result.isSuccess()) {
            showError(result.getErrorMessage());
            return null; // TODO точно null возвращать
        }
        return result.getData();
    }
}
