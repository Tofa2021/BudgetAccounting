package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.HouseholdMemberDTO;
import org.example.dto.request.household.CreateHouseholdMemberRequest;
import org.example.dto.request.household.DeleteHouseholdMemberRequest;
import org.example.dto.request.household_member.UpdateHouseholdMemberRoleRequest;

public class HouseholdMemberClient extends BaseClient {
    public HouseholdMemberClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<HouseholdMemberDTO> create(Long householdMemberId, Long userId, String role) {
        return sendAuthorizedRequest(new CreateHouseholdMemberRequest(householdMemberId, userId, role));
    }

    public Result<Void> updateRole(Long memberId, String newRole) {
        return sendAuthorizedRequest(new UpdateHouseholdMemberRoleRequest(memberId, newRole));
    }

    public Result<Void> delete(Long memberId) {
        return sendAuthorizedRequest(new DeleteHouseholdMemberRequest(memberId));
    }
}
