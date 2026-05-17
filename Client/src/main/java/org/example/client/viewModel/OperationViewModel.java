package org.example.client.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.client.SessionContext;
import org.example.client.connection.api.HouseholdClient;
import org.example.client.connection.api.OperationClient;
import org.example.client.screen.Screen;
import org.example.client.screen.ScreenLoader;
import org.example.dto.OperationDTO;

import java.util.Comparator;
import java.util.Objects;

@RequiredArgsConstructor
public class OperationViewModel extends BaseViewModel {
    private final ScreenLoader screenLoader;
    private final OperationClient operationClient;
    private final HouseholdClient householdClient;
    private final SessionContext sessionContext;
    @Getter
    private final ObservableList<OperationDTO> operations = FXCollections.observableArrayList();

    @Override
    public void onViewShown() {
        refreshOperations();
    }

    public void refreshOperations() {
        if (sessionContext.getCurrentHousehold().get() == null) {
            return;
        }

        var result = operationClient.getHouseholdOperations(sessionContext.getCurrentHousehold().get().getId());
        if (result.isSuccess()) {
            operations.setAll(result.getData().stream()
                    .sorted(Comparator.comparing(OperationDTO::getDateTime))
                    .toList()
                    .reversed());
        }
    }

    public void delete(OperationDTO operationDTO) {
        var result = operationClient.delete(operationDTO.getId());
        if (result.isSuccess()) {
            operations.remove(operationDTO);

            var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
            if (householdDTOResult.isSuccess()) {
                sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
            }
        }
    }

    public void update(OperationDTO operationDTO) {
        if (operationClient.update(operationDTO).isSuccess()) {
            operations.stream()
                    .filter(operationDTO1 -> Objects.equals(operationDTO1.getId(), operationDTO.getId()))
                    .findFirst()
                    .ifPresent(oldOperation -> {
                        int index = operations.indexOf(oldOperation);
                        operations.set(index, operationDTO);
                    });

            var householdDTOResult = householdClient.get(sessionContext.getCurrentHousehold().get().getId());
            if (householdDTOResult.isSuccess()) {
                sessionContext.getCurrentHousehold().set(householdDTOResult.getData());
            }
        }
    }

    public void handleCreatOperationButton() {
        screenLoader.load(Screen.CREATING_OPERATION);
    }
}
