package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

import java.util.Map;

@Getter
public class CreateHouseholdRequest extends AuthorizedRequest {
    private final String name;
    private final Map<Long, String> additionalMemberRoleMap;

    public CreateHouseholdRequest(String name, Map<Long, String> additionalMemberRoleMap) {
        super(RequestAction.CREATE_HOUSEHOLD);
        this.name = name;
        this.additionalMemberRoleMap = additionalMemberRoleMap;
    }
}
