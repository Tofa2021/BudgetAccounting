package org.example.client;

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
    private HouseholdDTO currentHousehold = null;
    private HouseholdMemberDTO currentHouseholdMember = null;
}
