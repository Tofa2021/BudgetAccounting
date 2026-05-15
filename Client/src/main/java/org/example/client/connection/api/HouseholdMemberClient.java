package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.HouseholdMemberDTO;
import org.example.request.RequestAction;

import java.util.Map;

public class HouseholdMemberClient extends BaseClient {
    public HouseholdMemberClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<HouseholdMemberDTO> create(Long householdId, String role, Long toCreateUserId) {
        return sendRequest(
                RequestAction.CREATE_HOUSEHOLD_MEMBER,
                Map.of(
                        "householdId", householdId,
                        "userId", toCreateUserId,
                        "role", role

                )
        );
    }

    public Result<Void> updateRole(Long id, String role) {
        return sendRequest(
                RequestAction.UPDATE_HOUSEHOLD_MEMBER_ROLE,
                Map.of(
                        "id", id,
                        "role", role

                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.DELETE_HOUSEHOLD_MEMBER,
                Map.of(
                        "id", id

                )
        );
    }
}
