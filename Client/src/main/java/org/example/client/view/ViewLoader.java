package org.example.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.example.client.viewModel.BaseViewModel;
import org.example.client.viewModel.ViewModelFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewLoader {
    private final ViewModelFactory viewModelFactory;
    private final Map<Class<? extends BaseView<?>>, Supplier<? extends BaseViewModel>> viewModelsSuppliers = new HashMap<>();

    public ViewLoader(ViewModelFactory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
        registerAll();
    }

    private void registerAll() {
        register(AuthView.class, viewModelFactory::createAuthViewModel);
        register(RegistrationView.class, viewModelFactory::createRegistrationViewModel);
        register(OperationView.class, viewModelFactory::createOperationViewModel);
    }

    private <V extends BaseView<VM>, VM extends BaseViewModel> void register(
            Class<V> viewClass,
            Supplier<VM> createViewModel
    ) {
        viewModelsSuppliers.put(viewClass, createViewModel);
    }

    public Parent loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Parent parent = loader.load();

            BaseView<BaseViewModel> controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Controller is null. Check fx:controller in " + fxmlPath);
            }

            Supplier<? extends BaseViewModel> supplier = viewModelsSuppliers.get(controller.getClass());
            if (supplier == null) {
                throw new RuntimeException("No ViewModel supplier registered for: " + controller.getClass());
            }

            BaseViewModel viewModel = supplier.get();
            controller.setViewModel(viewModel);

            return parent;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
