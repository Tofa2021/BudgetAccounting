package org.example.client;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.HouseholdDTO;
import org.example.dto.HouseholdMemberDTO;
import org.example.dto.UserDTO;

@Getter
@Setter
public class SessionContext {
    private String accessToken = "";
    private String refreshToken = "";
    private UserDTO currentUser = null;
    private ObjectProperty<HouseholdDTO> currentHousehold = new SimpleObjectProperty<>();
    private HouseholdMemberDTO currentHouseholdMember = null;
}
