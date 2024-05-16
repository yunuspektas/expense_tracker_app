package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.TransactionType;
import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.exception.BadRequestException;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.exception.UnauthorizedException;
import com.dbglobe.payload.mappers.AccountMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import com.dbglobe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ServiceHelper serviceHelper;

    @Transactional
    public AccountResponse createAccount(AccountRequest accountRequest, UserDetails userDetails) {
        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);

        Account newAccount = accountMapper.mapAccountRequestToAccount(accountRequest);
        newAccount.setBalance(BigDecimal.valueOf(0.0));
        newAccount.setCustomer(serviceHelper.getUserByUsername(userName));

        return accountMapper.mapAccountToAccountResponse(accountRepository.save(newAccount));
    }

    @Transactional
    public void deleteAccount(Long id, UserDetails userDetails) {
        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);;

        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));

        if(account.getCustomer().getUsername().equals(userName)) {
            accountRepository.delete(account);
        }else {
            throw new UnauthorizedException("You do not have permission to delete this account");
        }
    }

    @Transactional
    public AccountResponse updateAccount(Long id, AccountRequest accountDetails, UserDetails userDetails) {

        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);
        Account account = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Account not found"));
        Account savedAccount;
        if(account.getCustomer().getUsername().equals(userName)) {
            account.setAccountName(accountDetails.getAccountName());
            account.setAccountType(accountDetails.getAccountType());
            savedAccount = accountRepository.save(account);
        }else {
            throw new UnauthorizedException("You do not have permission to delete this account");
        }
        return accountMapper.mapAccountToAccountResponse(savedAccount);
    }

    public List<AccountResponse> getAll(UserDetails userDetails) {

        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);
        User customer = serviceHelper.getUserByUsername(userName);

        List<Account> accounts = accountRepository.findByCustomerId(customer.getId());
        return accounts.stream()
                .map(accountMapper::mapAccountToAccountResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Account deposit(Long accountId, UserDetails userDetails, BigDecimal amount, String category) {

        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessages.ACCOUNT_NOT_FOUND_WITH_ID, accountId)));

        Account savedAccount;
        if(account.getCustomer().getUsername().equals(userName)) {
            account.setBalance(account.getBalance().add(amount));
            savedAccount = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAccount(savedAccount);
            transaction.setAmount(amount);
            transaction.setDate(LocalDate.now());
            transaction.setType(TransactionType.DEPOSIT);
            transaction.setCategory(category);
            transactionRepository.save(transaction);
        }else {
            throw new UnauthorizedException("You do not have permission to delete this account");
        }
        return savedAccount;
    }

    @Transactional
    public Account withdraw(Long accountId, UserDetails userDetails, BigDecimal amount, String category) {

        String userName = serviceHelper.getUserNameFromUserDetails(userDetails);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException(String.format(ErrorMessages.ACCOUNT_NOT_FOUND_WITH_ID, accountId)));

        Account savedAccount;
        if(account.getCustomer().getUsername().equals(userName)) {
            BigDecimal newBalance = account.getBalance().subtract(amount);
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(ErrorMessages.INSUFFICIENT_AMOUNT_MESSAGE);
            }

            account.setBalance(newBalance);
            savedAccount = accountRepository.save(account);

            Transaction transaction = new Transaction();
            transaction.setAccount(savedAccount);
            transaction.setAmount(amount.negate());
            transaction.setDate(LocalDate.now());
            transaction.setType(TransactionType.WITHDRAWAL);
            transaction.setCategory(category); // Category comes from the user input
            transactionRepository.save(transaction);
        }else {
            throw new UnauthorizedException("You do not have permission to delete this account");
        }
        return savedAccount;
    }
}



