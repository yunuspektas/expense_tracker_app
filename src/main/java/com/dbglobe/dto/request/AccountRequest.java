package com.dbglobe.dto.request;

import com.dbglobe.domain.enums.AccountType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountRequest {

    private String accountName;

    private AccountType accountType;
}
