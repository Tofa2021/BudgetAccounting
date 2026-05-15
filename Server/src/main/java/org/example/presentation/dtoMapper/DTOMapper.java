package org.example.presentation.dtoMapper;

import org.example.domain.model.*;
import org.example.dto.*;

import java.util.List;
import java.util.function.Function;

public interface DTOMapper {
    OperationDTO toOperationDTO(Operation operation);

    AccountDTO toAccountDTO(Account account);

    AccountMemberDTO toAccountMemberDTO(AccountMember member);

    CategoryDTO toCategoryDTO(Category category);

    HouseholdDTO toHouseholdDTO(Household household);

    HouseholdMemberDTO toHouseholdMemberDTO(HouseholdMember member);

    UserDTO toUserDTO(User user);

    UserPublicDTO toUserPublicDTO(User user);

    default List<OperationDTO> toOperationDTOs(List<Operation> operations) {
        return toDTOs(operations, this::toOperationDTO);
    }

    default List<AccountDTO> toAccountDTOs(List<Account> accounts) {
        return toDTOs(accounts, this::toAccountDTO);
    }

    default List<AccountMemberDTO> toAccountMemberDTOs(List<AccountMember> members) {
        return toDTOs(members, this::toAccountMemberDTO);
    }

    default List<CategoryDTO> toCategoryDTOs(List<Category> categories) {
        return toDTOs(categories, this::toCategoryDTO);
    }

    default List<HouseholdDTO> toHouseholdDTOs(List<Household> households) {
        return toDTOs(households, this::toHouseholdDTO);
    }

    default List<HouseholdMemberDTO> toHouseholdMemberDTOs(List<HouseholdMember> members) {
        return toDTOs(members, this::toHouseholdMemberDTO);
    }

    default List<UserDTO> toUserDTOs(List<User> users) {
        return toDTOs(users, this::toUserDTO);
    }

    default List<UserPublicDTO> toUserPublicDTOs(List<User> users) {
        return toDTOs(users, this::toUserPublicDTO);
    }

    default <T extends BaseModel, R extends DTO> List<R> toDTOs(List<T> models, Function<T, R> mapper) {
        if (models == null) return List.of();
        return models.stream().map(mapper).toList();
    }
}
