package org.example.util;

import org.example.domain.model.*;
import org.example.dto.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class DTOMapperImpl implements DTOMapper {
    private final Map<Class<? extends BaseModel>, Function<BaseModel, DTO>> modelToDTOMap = new ConcurrentHashMap<>();

    public DTOMapperImpl() {
        modelToDTOMap.put(Operation.class, (model) -> createOperationDTO((Operation) model));
        modelToDTOMap.put(Account.class, (model) -> createAccountDTO((Account) model));
        modelToDTOMap.put(AccountMember.class, (model) -> createAccountMemberDTO((AccountMember) model));
        modelToDTOMap.put(Category.class, (model) -> createCategory((Category) model));
        modelToDTOMap.put(Household.class, (model) -> createHouseholdDTO((Household) model));
        modelToDTOMap.put(HouseholdMember.class, (model) -> createHouseholdMemberDTO((HouseholdMember) model));
        modelToDTOMap.put(User.class, (model) -> createUserDTO((User) model));
    }

    @Override
    public <T extends BaseModel, R extends DTO> R toDTO(T model, Class<R> dtoClass) {
        Function<BaseModel, DTO> mapper = modelToDTOMap.get(model.getClass());
        return (R) mapper.apply(model);
    }

    @Override
    public <T extends DTO, R extends BaseModel> R fromDTO(T dto, Class<R> modelClass) {
        return null;
    }

    private OperationDTO createOperationDTO(Operation operation) {
        return new OperationDTO(
                operation.getId(),
                operation.getAccount().getId(),
                operation.getDescription(),
                operation.getAmount(),
                operation.getDateTime(),
                operation.getUser().getId(),
                operation.getCategory().getId()
        );
    }

    private AccountDTO createAccountDTO(Account account) {
        return new AccountDTO(
                account.getId(),
                account.getName(),
                account.getCurrency(),
                account.getAmount(),
                convertToIdList(account.getMembers()),
                account.getHousehold().getId()
        );
    }

    private AccountMemberDTO createAccountMemberDTO(AccountMember accountMember) {
        return new AccountMemberDTO(
                accountMember.getId(),
                accountMember.getRole().name(),
                accountMember.getHouseholdMember().getId(),
                accountMember.getAccount().getId()
        );
    }

    private CategoryDTO createCategory(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getType().name(),
                category.getHousehold().getId()
        );
    }

    private HouseholdDTO createHouseholdDTO(Household household) {
        return new HouseholdDTO(
                household.getId(),
                household.getName(),
                convertToIdList(household.getMembers()),
                convertToIdList(household.getAccounts()),
                convertToIdList(household.getCategories())
        );
    }

    private HouseholdMemberDTO createHouseholdMemberDTO(HouseholdMember householdMember) {
        return new HouseholdMemberDTO(
                householdMember.getId(),
                householdMember.getUser().getId(),
                householdMember.getHousehold().getId(),
                convertToIdList(householdMember.getAccountMembers()),
                householdMember.getRole().name()
        );
    }

    private UserDTO createUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                convertToIdList(user.getMembers())
        );
    }

    private List<Long> convertToIdList(List<? extends BaseModel> elements) {
        return elements
                .stream()
                .map(BaseModel::getId)
                .toList();
    }
}