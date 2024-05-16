package com.dbglobe.dto.request;

import lombok.*;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Value
public class DepositWithDrawRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    BigDecimal amount;
    String category;
}
