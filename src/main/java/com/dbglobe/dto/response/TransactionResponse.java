package com.dbglobe.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class TransactionResponse {

    LocalDate date;
    BigDecimal amount;
    String category; // Enum Olabilir
}
