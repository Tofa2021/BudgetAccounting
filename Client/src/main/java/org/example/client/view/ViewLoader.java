package org.example.client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.example.Pair;
import org.example.client.viewModel.BaseViewModel;
import org.example.client.viewModel.ViewModelFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ViewLoader {
    private final ViewModelFactory viewModelFactory;
    private final Map<Class<?>, Supplier<? extends BaseViewModel>> viewModelsSuppliers = new HashMap<>();
    private ApplicationView applicationView;

    public ViewLoader(ViewModelFactory viewModelFactory) {
        this.viewModelFactory = viewModelFactory;
        registerAll();
    }

    private void registerAll() {
        register(AuthView.class, viewModelFactory::createAuthViewModel);
        register(RegistrationView.class, viewModelFactory::createRegistrationViewModel);
        register(OperationView.class, viewModelFactory::createOperationViewModel);
        register(NavigationView.class, viewModelFactory::createNavigationViewModel);
        register(HeaderView.class, viewModelFactory::createHeaderViewModel);
        register(CreatingOperationView.class, viewModelFactory::createCreatingOperationViewModel);
        register(HouseholdView.class, viewModelFactory::createHouseholdViewModel);
        register(CreatingHouseholdView.class, viewModelFactory::createCreatingHouseholdViewModel);
    }

    private <V extends BaseView<VM>, VM extends BaseViewModel> void register(
            Class<V> viewClass,
            Supplier<VM> createViewModel
    ) {
        viewModelsSuppliers.put(viewClass, createViewModel);
    }

    public <T extends FXMLLoadable> Pair<Parent, T> load(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Parent parent = loader.load();

            T controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Controller is null. Check fx:controller in " + fxmlPath);
            }

            return new Pair<>(parent, controller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <VM extends BaseViewModel> VM createViewModel(Class<?> viewClass) {
        Supplier<? extends BaseViewModel> supplier = viewModelsSuppliers.get(viewClass);
        if (supplier == null) {
            throw new RuntimeException("No ViewModel supplier registered for: " + viewClass);
        }
        return (VM) supplier.get();
    }

    public <V extends BaseView<VM>, VM extends BaseViewModel> Pair<Parent, V> loadBoundView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

            Parent parent = loader.load();

            V controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Controller is null. Check fx:controller in " + fxmlPath);
            }

            Supplier<? extends BaseViewModel> supplier = viewModelsSuppliers.get(controller.getClass());
            if (supplier == null) {
                throw new RuntimeException("No ViewModel supplier registered for: " + controller.getClass());
            }

            controller.setApplicationView(applicationView);

            Class<?> controllerClass = controller.getClass();
            VM viewModel = createViewModel(controllerClass);

            controller.setViewModel(viewModel);
            return new Pair<>(parent, controller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pair<Parent, ApplicationView> loadApplicationView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/client/view/ApplicationView.fxml"));
            loader.setControllerFactory(clazz -> new ApplicationView(viewModelFactory.createApplicationViewModel()));

            Parent parent = loader.load();

            ApplicationView controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Controller is null");
            }

            this.applicationView = controller;
            return new Pair<>(parent, controller);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
