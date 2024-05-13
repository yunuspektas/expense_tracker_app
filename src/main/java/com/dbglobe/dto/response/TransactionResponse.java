package com.dbglobe.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private LocalDate date;
    private BigDecimal amount;
    private String category; // Enum Olabilir
}
