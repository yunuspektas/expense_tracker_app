package com.dbglobe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepositWithDrawRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    private BigDecimal amount;
    private String category;
}
