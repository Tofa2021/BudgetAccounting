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
        register(AccountView.class, viewModelFactory::createAccountViewModel);
        register(CreatingAccountView.class, viewModelFactory::createCreatingAccountViewModel);
        register(GraphicsView.class, viewModelFactory::createGraphicsViewModel);
        register(ExpensePieChartView.class, viewModelFactory::createExpensePieChartViewModel);
        register(PeriodSelectorView.class, viewModelFactory::createPeriodSelectorViewModel);
    }

    public Pair<Parent, GraphicsView> loadBoundGraphicsViewModel() {
        Pair<Parent, PeriodSelectorView> selectorPair = loadBoundView("/org/example/client/component/PeriodSelector.fxml");
        PeriodSelectorView periodSelector = selectorPair.getSecond();
        periodSelector.show();

        Pair<Parent, ExpensePieChartView> pieChartPair = loadBoundView("/org/example/client/component/ExpensePieChart.fxml");
        ExpensePieChartView pieChart = pieChartPair.getSecond();
        pieChart.show();

        pieChart.setPeriodSelector(periodSelector, selectorPair.getFirst());

        Pair<Parent, GraphicsView> graphicsViewPair = loadBoundView("/org/example/client/view/GraphicsView.fxml");
        GraphicsView graphicsView = graphicsViewPair.getSecond();

        graphicsView.setExpensePieChart(pieChartPair.getFirst());

        return new Pair<>(graphicsViewPair.getFirst(), graphicsView);
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
