package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.AccountType;
import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.exception.UnauthorizedException;
import com.dbglobe.payload.mappers.AccountMapper;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import com.dbglobe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserDetails userDetails;
    @Mock
    private ServiceHelper serviceHelper;
    @InjectMocks
    private AccountService accountService;
    private AccountRequest accountRequest;
    private Account account;
    private User user;
    private AccountResponse accountResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");

        accountRequest = AccountRequest.builder()
                .accountName("Saving")
                .accountType(AccountType.EURO)
                .build();

        account = new Account();
        account.setId(1L);
        account.setCustomer(user);
        account.setAccountName("Saving");
        account.setAccountType(AccountType.EURO);
        account.setBalance(BigDecimal.ZERO);

        accountResponse = AccountResponse.builder()
                .accountId(1L)
                .accountName("Saving")
                .accountType(AccountType.EURO)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void whenCreateAccount_thenAccountIsSavedSuccessfully() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountMapper.mapAccountRequestToAccount(accountRequest)).willReturn(account);
        given(accountRepository.save(account)).willReturn(account);
        given(accountMapper.mapAccountToAccountResponse(account)).willReturn(accountResponse);

        AccountResponse createdAccount = accountService.createAccount(accountRequest, userDetails);

        assertThat(createdAccount.getAccountName()).isEqualTo("Saving");
        assertThat(createdAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
        verify(accountRepository).save(account);
    }

    @Test
    void whenCreateAccountWithNonExistUser_thenThrowsException() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("nonexistent");
        assertThrows(NullPointerException.class, () -> {
            accountService.createAccount(accountRequest, userDetails);
        });

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void whenDeleteAccount_thenDeleteTheAccount() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        accountService.deleteAccount(1L, userDetails);

        verify(accountRepository).delete(account);
    }

    @Test
    void whenDeleteAccountWithNonExistentAccount_thenThrowException() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(1L, userDetails);
        });

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void whenDeleteAccountWithUnauthorizedUser_thenThrowException() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        account.setCustomer(anotherUser);
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        assertThrows(UnauthorizedException.class, () -> {
            accountService.deleteAccount(1L, userDetails);
        });

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void whenUpdateAccount_thenAccountUpdatedSuccessfully() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willReturn(account);
        given(accountMapper.mapAccountToAccountResponse(account)).willReturn(accountResponse);

        AccountResponse updatedAccount = accountService.updateAccount(1L, accountRequest, userDetails);

        assertThat(updatedAccount.getAccountName()).isEqualTo("Saving");
        verify(accountRepository).save(account);
    }

    @Test
    void whenUpdateNonExistentAccount_thenThrowException() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            accountService.updateAccount(1L, accountRequest, userDetails);
        });

        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void whenGetAll_thenReturnAccountList() {

        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(serviceHelper.getUserByUsername("testUser")).willReturn(user);
        given(accountRepository.findByCustomerId(user.getId())).willReturn(Collections.singletonList(account));
        given(accountMapper.mapAccountToAccountResponse(account)).willReturn(accountResponse);

        List<AccountResponse> accounts = accountService.getAll(userDetails);

        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountName()).isEqualTo("Saving");
    }

    @Test
    void whenDeposit_thenAccountBalanceShouldIncrease() {

        BigDecimal depositAmount = BigDecimal.valueOf(500);
        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willReturn(account);

        Account updatedAccount = accountService.deposit(1L, userDetails, depositAmount, "Salary");

        assertThat(updatedAccount.getBalance()).isEqualByComparingTo(BigDecimal.ZERO.add(depositAmount));
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void whenDepositToNonExistentAccount_thenThrowException() {

        BigDecimal depositAmount = BigDecimal.valueOf(500);
        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.deposit(1L, userDetails, depositAmount, "Salary");
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void whenWithdraw_thenAccountBalanceShouldDecrease() {

        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        account.setBalance(BigDecimal.valueOf(1000));
        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(account)).willReturn(account);

        Account updatedAccount = accountService.withdraw(1L, userDetails, withdrawAmount, "Utility");

        assertThat(updatedAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000).subtract(withdrawAmount));
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void whenWithdrawFromNonExistentAccount_thenThrowException() {

        BigDecimal withdrawAmount = BigDecimal.valueOf(500);
        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(1L, userDetails, withdrawAmount, "Utility");
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void whenWithdrawWithInsufficientFunds_thenThrowException() {

        BigDecimal withdrawAmount = BigDecimal.valueOf(1500);
        account.setBalance(BigDecimal.valueOf(1000));
        given(serviceHelper.getUserNameFromUserDetails(userDetails)).willReturn("testUser");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(1L, userDetails, withdrawAmount, "Utility");
        });

        verify(accountRepository, never()).save(any(Account.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}
