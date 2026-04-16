package com.cooperative.banking_system.mapper;


import com.cooperative.banking_system.dto.AccountDTO;
import com.cooperative.banking_system.entity.Accounts;

public class DtoMapper {


    public static AccountDTO toAccountDTO(Accounts accounts) {
        return new AccountDTO(accounts.getAccountId(), accounts.getName(), accounts.getType(), accounts.getBalance(), accounts.getCreateDate()
        );
    }
}
