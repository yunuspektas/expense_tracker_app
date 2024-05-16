package com.dbglobe.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Value
@Builder
public class TransactionRequest {

    LocalDate date = LocalDate.now();
    BigDecimal amount;
    String category; // Enum olabilir
}
