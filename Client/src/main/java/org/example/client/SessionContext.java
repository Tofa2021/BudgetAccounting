package org.example.client;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.HouseholdDTO;
import org.example.dto.HouseholdMemberDTO;
import org.example.dto.UserDTO;

@Getter
@Setter
public class SessionContext {
    private StringProperty accessToken = new SimpleStringProperty("");
    private StringProperty refreshToken = new SimpleStringProperty("");
    private ObjectProperty<UserDTO> currentUser = new SimpleObjectProperty<>();
    private ObjectProperty<HouseholdDTO> currentHousehold = new SimpleObjectProperty<>();
    private ObjectProperty<HouseholdMemberDTO> currentHouseholdMember = new SimpleObjectProperty<>();

    public void clear() {
        accessToken.set("");
        refreshToken.set("");
        currentUser.set(null);
        currentHousehold.set(null);
        currentHouseholdMember.set(null);
    }
}
