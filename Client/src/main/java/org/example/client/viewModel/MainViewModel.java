package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.example.client.model.*;

@Getter
public class MainViewModel extends BaseViewModel {
    private final StringProperty incomeAmount = new SimpleStringProperty("3 254,20");
    private final StringProperty expenseAmount = new SimpleStringProperty("-2 022,42");
    
    private final ObservableList<CategoryItem> incomeCategories = FXCollections.observableArrayList();
    private final ObservableList<CategoryItem> expenseCategories = FXCollections.observableArrayList();
    private final ObservableList<TrendItem> trends = FXCollections.observableArrayList();
    private final ObservableList<MemberItem> members = FXCollections.observableArrayList();
    private final ObservableList<OperationItem> operations = FXCollections.observableArrayList();
    private final ObservableList<LimitItem> limits = FXCollections.observableArrayList();

    public MainViewModel() {
        initMockData();
    }

    private void initMockData() {
        // Income categories
        incomeCategories.addAll(
                new CategoryItem("💰", "Зарплата", "1 200,20 BYN", "33%"),
                new CategoryItem("🔄", "Вернули долг", "540 BYN", "23%"),
                new CategoryItem("💻", "Фриланс", "350 BYN", "20%"),
                new CategoryItem("📈", "Дивиденды", "240 BYN", "11%"),
                new CategoryItem("🎁", "Подарок", "100 BYN", "5%"),
                new CategoryItem("🔧", "Подработка", "35 BYN", "2%")
        );

        // Expense categories
        expenseCategories.addAll(
                new CategoryItem("💼", "Зарплата", "1 200,20 BYN", "23%"),
                new CategoryItem("🍕", "Еда", "1 001 BYN", "19%"),
                new CategoryItem("🚗", "Транспорт", "520 BYN", "10%"),
                new CategoryItem("👕", "Одежда", "200 BYN", "9%"),
                new CategoryItem("📱", "Связь", "150 BYN", "5%"),
                new CategoryItem("💊", "Здоровье", "133 BYN", "4%")
        );

        // Trends
        trends.addAll(
                new TrendItem("Доходы: +5 %\nПрошлый месяц: 3092,32"),
                new TrendItem("Расходы: +2 %\nПрошлый месяц: 1982,76"),
                new TrendItem("Разница: 1 231,78 (+9 %)\nПрошлый месяц: 1 126,80")
        );

        // Members
        members.addAll(
                new MemberItem("Иван", "Владелец"),
                new MemberItem("Петр", "Менеджер"),
                new MemberItem("Александр", "Менеджер"),
                new MemberItem("Василий", "Участник"),
                new MemberItem("Кирилл", "Участник"),
                new MemberItem("Богдан", "Участник"),
                new MemberItem("Георгий", "Участник")
        );

        // Limits
        limits.addAll(
                new LimitItem("Развлечения", 23, 100),
                new LimitItem("Транспорт", 55, 120),
                new LimitItem("Ресторан", 100, 150),
                new LimitItem("Еда", 123, 560),
                new LimitItem("Подарки", 200, 350)
        );
    }

    public void loadData() {
        // Load data from server
    }

    public void onInviteClicked() {
        // Handle invite button click
    }

    // Getters for properties
    public StringProperty incomeAmountProperty() {
        return incomeAmount;
    }

    public StringProperty expenseAmountProperty() {
        return expenseAmount;
    }

    @Override
    public void onViewShown() {

    }
}
