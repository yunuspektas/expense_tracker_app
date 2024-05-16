package com.dbglobe.dto.request;

import com.dbglobe.domain.enums.AccountType;
import lombok.*;

@Value
@Builder
public class AccountRequest {

    String accountName;

    AccountType accountType;
}
