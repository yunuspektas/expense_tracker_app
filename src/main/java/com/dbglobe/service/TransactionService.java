package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.enums.TransactionType;
import com.dbglobe.dto.request.TransactionRequest;
import com.dbglobe.dto.response.TransactionResponse;
import com.dbglobe.payload.mappers.TransactionMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.payload.messages.SuccessMessages;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionJpaRepository;
    private final AccountRepository accountRepository ; // TODO :bu kisimi AccountService uzerinden hallet
    private final TransactionMapper transactionMapper;


    public List<TransactionResponse> getAllTransactionsByAccount(Long accountId) {
        List<Transaction> transactions = transactionJpaRepository.findByAccountId(accountId);

        return transactions.stream()
                .map(transactionMapper::mapTransactionToTransactionResponse)
                .collect(Collectors.toList());

    }

    public String deleteTransaction(Long id) {
        Transaction transaction = transactionJpaRepository.findById(id).orElseThrow(() ->
                new RuntimeException(String.format(ErrorMessages.TRANSACTION_NOT_FOUND_WITH_ID, id)));

        // update account's balance
        Account account = transaction.getAccount();
        if(transaction.getType().equals(TransactionType.DEPOSIT)){
            account.setBalance(account.getBalance().subtract(transaction.getAmount()));
        } else  account.setBalance(account.getBalance().add(transaction.getAmount()));
        accountRepository.save(account);

        transactionJpaRepository.delete(transaction);
        return SuccessMessages.TRANSACTION_DELETE;
    }

    public List<TransactionResponse> searchTransactions(String category, LocalDate startDate, LocalDate endDate) {

        return transactionJpaRepository.findByCategoryContainingAndDateBetween(category, startDate, endDate)
                .stream()
                .map(transactionMapper::mapTransactionToTransactionResponse)
                .collect(Collectors.toList());
    }
}
