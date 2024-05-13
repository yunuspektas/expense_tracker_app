package com.dbglobe.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionRequest {

    private LocalDate date = LocalDate.now();
    private BigDecimal amount;
    private String category; // Enum olabilir
}
