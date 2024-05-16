package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.TransactionType;
import com.dbglobe.dto.response.TransactionResponse;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.exception.UnauthorizedException;
import com.dbglobe.payload.mappers.TransactionMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionJpaRepository;
    private final AccountRepository accountRepository ; // TODO :bu kisimi AccountService uzerinden hallet
    private final TransactionMapper transactionMapper;
    private final ServiceHelper serviceHelper;


    public List<TransactionResponse> getAllTransactionsByAccount(Long accountId, UserDetails userDetails) {
        String userName = userDetails.getUsername();
        User customer = serviceHelper.getUserByUsername(userName);

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Account not found"));
        List<Transaction> transactions ;
        if(account.getCustomer().getUsername().equals(userName)) {
            transactions = transactionJpaRepository.findByAccountId(accountId);
        }else {
            throw new UnauthorizedException("You do not have permission to get this account's transactions");
        }

        return transactions.stream()
                .map(transactionMapper::mapTransactionToTransactionResponse)
                .collect(Collectors.toList());

    }

    @Transactional
    public void deleteTransaction(Long id, UserDetails userDetails) {
        Transaction transaction = transactionJpaRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(String.format(ErrorMessages.TRANSACTION_NOT_FOUND_WITH_ID, id)));
        // update account's balance
        Account account = transaction.getAccount();
        if(account.getCustomer().getUsername().equals(userDetails.getUsername())) {
            if(transaction.getType().equals(TransactionType.DEPOSIT)){
                account.setBalance(account.getBalance().subtract(transaction.getAmount()));
            } else  account.setBalance(account.getBalance().add(transaction.getAmount()));

            accountRepository.save(account);
            transactionJpaRepository.delete(transaction);
        }else {
            throw new UnauthorizedException("You do not have permission to delete this account's transaction");
        }
    }

    public List<TransactionResponse> searchTransactions(Long accountId, String category, LocalDate startDate, LocalDate endDate, UserDetails userDetails) {

        Account account = accountRepository.findById(accountId).orElseThrow(()->
                new ResourceNotFoundException("Account is not found with id : " + accountId));

        User customer = serviceHelper.getUserByUsername(userDetails.getUsername());

        if(account.getCustomer().getUsername().equals(userDetails.getUsername())) {
            return transactionJpaRepository.findByAccount_Customer_IdAndAccount_IdAndCategoryContainingAndDateBetween(
                            customer.getId(), accountId, category, startDate, endDate)
                    .stream()
                    .map(transactionMapper::mapTransactionToTransactionResponse)
                    .collect(Collectors.toList());
        }else {
            throw new UnauthorizedException("You do not have permission to search this account's transaction");
        }
    }
}
