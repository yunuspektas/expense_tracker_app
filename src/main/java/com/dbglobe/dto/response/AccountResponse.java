package com.dbglobe.dto.response;

import com.dbglobe.domain.enums.AccountType;
import lombok.*;
import java.math.BigDecimal;

@Value
@Builder
public class AccountResponse {

    Long accountId;

    String accountName;

    AccountType accountType;

    BigDecimal balance;
}
