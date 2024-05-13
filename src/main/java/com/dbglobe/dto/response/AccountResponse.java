package com.dbglobe.dto.response;

import com.dbglobe.domain.enums.AccountType;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {

    private String accountName;

    private AccountType accountType;

    private BigDecimal balance;
}
