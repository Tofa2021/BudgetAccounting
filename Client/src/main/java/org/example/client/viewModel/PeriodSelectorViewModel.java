package org.example.client.viewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public class PeriodSelectorViewModel extends BaseViewModel {
    @Getter
    private final ObjectProperty<Period> selectedPeriod = new SimpleObjectProperty<>();

    @Override
    public void onViewShown() {
        selectedPeriod.set(Period.WEEK);
    }

    public void handleDayButton() {
        selectedPeriod.set(Period.DAY);
    }

    public void handleWeekButton() {
        selectedPeriod.set(Period.WEEK);
    }

    public void handleMonthButton() {
        selectedPeriod.set(Period.MONTH);
    }

    public void handleYearButton() {
        selectedPeriod.set(Period.YEAR);
    }

    public void handleAllButton() {
        selectedPeriod.set(Period.ALL);
    }
}
