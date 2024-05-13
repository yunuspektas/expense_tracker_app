package com.dbglobe.payload.mappers;

import com.dbglobe.domain.Transaction;
import com.dbglobe.dto.response.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse mapTransactionToTransactionResponse(Transaction transaction) {

        return TransactionResponse.builder()
                .date(transaction.getDate())
                .amount(transaction.getAmount())
                .category(transaction.getCategory())
                .build();
    }
}
