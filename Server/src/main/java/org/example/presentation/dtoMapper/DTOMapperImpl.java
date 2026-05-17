package org.example.presentation.dtoMapper;

import org.example.domain.model.*;
import org.example.dto.*;

import java.util.List;

public class DTOMapperImpl implements DTOMapper {
    private List<Long> convertToIdList(List<? extends BaseModel> elements) {
        return elements
                .stream()
                .map(BaseModel::getId)
                .toList();
    }

    @Override
    public OperationDTO toOperationDTO(Operation operation) {
        return new OperationDTO(
                operation.getId(),
                operation.getAccount().getId(),
                operation.getDescription(),
                operation.getAmount(),
                operation.getDateTime(),
                operation.getAccountMember().getId(),
                operation.getCategory().getId(),
                operation.getCategory().getName(),
                operation.getCategory().getType().name(),
                operation.getAccount().getCurrency()
        );
    }

    @Override
    public AccountDTO toAccountDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getName(),
                account.getCurrency(),
                account.getAmount(),
                convertToIdList(account.getMembers()),
                account.getHousehold().getId()
        );
    }

    @Override
    public AccountMemberDTO toAccountMemberDTO(AccountMember member) {
        return new AccountMemberDTO(
                member.getId(),
                member.getRole().name(),
                member.getHouseholdMember().getId(),
                member.getAccount().getId()
        );
    }

    @Override
    public CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getType().name(),
                category.getHousehold().getId()
        );
    }

    @Override
    public HouseholdDTO toHouseholdDTO(Household household) {
        return new HouseholdDTO(
                household.getId(),
                household.getName(),
                convertToIdList(household.getMembers()),
                convertToIdList(household.getAccounts()),
                convertToIdList(household.getCategories())
        );
    }

    @Override
    public HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember member) {
        return new HouseholdMemberDTO(
                member.getId(),
                member.getUser().getId(),
                member.getHousehold().getId(),
                convertToIdList(member.getAccountMembers()),
                member.getRole().name()
        );
    }

    @Override
    public UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                convertToIdList(user.getMembers())
        );
    }

    @Override
    public UserPublicDTO toUserPublicDTO(User user) {
        return new UserPublicDTO(
                user.getId(),
                user.getUsername()
        );
    }
}