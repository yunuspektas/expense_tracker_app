package com.dbglobe.payload.mappers;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.dto.response.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountResponse mapAccountToAccountResponse(Account account) {

        return AccountResponse.builder()
                .accountId(account.getId())
                .accountName(account.getAccountName())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .build();
    }

    public Account mapAccountRequestToAccount(AccountRequest accountRequest){
        return Account.builder()
                .accountName(accountRequest.getAccountName())
                .accountType(accountRequest.getAccountType())
                .build();
    }
}
