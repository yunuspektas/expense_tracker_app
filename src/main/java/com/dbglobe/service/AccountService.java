package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.TransactionType;
import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.payload.mappers.AccountMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.payload.messages.SuccessMessages;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import com.dbglobe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository; // TODO : UserServcie uzerinden halledilecek
    private final TransactionRepository transactionRepository;

    public AccountResponse saveAccount(AccountRequest accountRequest, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");
        User customer = userRepository.findByUsername(userName).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_WITH_USERNAME, userName)));

        Account newAccount = accountMapper.mapAccountRequestToAccount(accountRequest);
        newAccount.setBalance(BigDecimal.valueOf(0.0));
        newAccount.setCustomer(customer);

        return accountMapper.mapAccountToAccountResponse(accountRepository.save(newAccount));

    }

    public String deleteAccount(Long id, HttpServletRequest request) {
        String userName = (String) request.getAttribute("username");

        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        if(account.getCustomer().getUsername().equals(userName)) {
            accountRepository.delete(account);
            return SuccessMessages.ACCOUNT_DELETE;
        } else return ErrorMessages.WRONG_ACCOUNT_MESSAGE;
    }

    public AccountResponse updateAccount(Long id, AccountRequest accountDetails) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        account.setAccountName(accountDetails.getAccountName());
        account.setAccountType(accountDetails.getAccountType());
        Account savedAccount = accountRepository.save(account);

        return accountMapper.mapAccountToAccountResponse(savedAccount);
    }

    public List<AccountResponse> getAll(HttpServletRequest request) {

        String userName = (String) request.getAttribute("username");
        User customer = userRepository.findByUsername(userName).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_WITH_USERNAME, userName)));

        List<Account> accounts = accountRepository.findByCustomerId(customer.getId());
        return accounts.stream()
                .map(accountMapper::mapAccountToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Account deposit(Long accountId, BigDecimal amount, String category) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ACCOUNT_NOT_FOUND_WITH_ID, accountId)));

        account.setBalance(account.getBalance().add(amount));
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(savedAccount);
        transaction.setAmount(amount);
        transaction.setDate(LocalDate.now());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setCategory(category); // Category comes from the user input
        transactionRepository.save(transaction);

        return savedAccount;
    }

    @Transactional
    public Account withdraw(Long accountId, BigDecimal amount, String category) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(ErrorMessages.ACCOUNT_NOT_FOUND_WITH_ID, accountId)));

        BigDecimal newBalance = account.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_AMOUNT_MESSAGE);
        }

        account.setBalance(newBalance);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccount(savedAccount);
        transaction.setAmount(amount.negate());
        transaction.setDate(LocalDate.now());
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setCategory(category); // Category comes from the user input
        transactionRepository.save(transaction);

        return savedAccount;
    }
}



