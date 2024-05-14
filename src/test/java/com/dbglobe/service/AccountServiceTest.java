package com.dbglobe.service;

import com.dbglobe.domain.Account;
import com.dbglobe.domain.Transaction;
import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.AccountType;
import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.payload.mappers.AccountMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.payload.messages.SuccessMessages;
import com.dbglobe.repository.AccountRepository;
import com.dbglobe.repository.TransactionRepository;
import com.dbglobe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;
    private AccountRequest accountRequest;
    private Account account;
    private User user;
    private AccountResponse accountResponse;
    private MockHttpServletRequest request;
    private BigDecimal withdrawAmount = new BigDecimal("500.00");
    private BigDecimal initialBalance = new BigDecimal("1500.00");

    @BeforeEach
    void setUp() {

        user = new User();
        user.setUsername("testUser");

        account = new Account();
        account.setId(1L);
        account.setCustomer(user);
        account.setAccountName("Existing Account");
        account.setAccountType(AccountType.EURO);
        account.setBalance(BigDecimal.valueOf(1000));

        accountRequest = new AccountRequest("Saving", AccountType.EURO);
        account = new Account();
        account.setAccountName("Saving");
        account.setBalance(BigDecimal.ZERO);
        account.setCustomer(user);

        accountResponse = new AccountResponse("Saving", AccountType.EURO, BigDecimal.ZERO);

        request = new MockHttpServletRequest();
        request.setAttribute("username", "testUser");
    }

    // ********************* TEST FOR CREATE METHOD ***********************************
    @Test
    void whenSaveAccount_thenAccountIsSavedSuccessfully() {
        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(user));
        given(accountMapper.mapAccountRequestToAccount(accountRequest)).willReturn(account);
        given(accountRepository.save(account)).willReturn(account);
        given(accountMapper.mapAccountToAccountResponse(account)).willReturn(accountResponse);

        AccountResponse createdAccount = accountService.saveAccount(accountRequest, request);

        assertThat(createdAccount.getAccountName()).isEqualTo("Saving");
        assertThat(createdAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
        verify(accountRepository).save(account);
    }

    @Test
    void whenSaveAccountWithNonExistUser_thenThrowsException() {
        given(userRepository.findByUsername("nonexistent")).willReturn(Optional.empty());
        request.setAttribute("username", "nonexistent");

        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.saveAccount(accountRequest, request);
        });
    }

    // ********************* TEST FOR DELETE METHOD ***********************************
    @Test
    void whenDeleteAccount_thenDeleteTheAccount() {

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        String result = accountService.deleteAccount(1L, request);

        assertThat(result).isEqualTo(SuccessMessages.ACCOUNT_DELETE);
        verify(accountRepository).delete(account);
    }

    @Test
    void whenDeleteAccountWithNonExistentAccount_thenThrowException() {

        given(accountRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            accountService.deleteAccount(1L, request);
        });

        verify(accountRepository, never()).delete(any(Account.class));
    }

    @Test
    void whenDeleteAccountWithUnauthorizedUser_thenReturnErrorMessage() {

        User differentUser = new User();
        differentUser.setUsername("anotherUser");
        account.setCustomer(differentUser);

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        String result = accountService.deleteAccount(1L, request);

        assertThat(result).isEqualTo(ErrorMessages.WRONG_ACCOUNT_MESSAGE);
        verify(accountRepository, never()).delete(any(Account.class));
    }

    // ********************* TEST FOR UPDATE METHOD ******************************
    @Test
    void whenUpdateAccount_thenAccountUpdatedSuccessfully() {

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);
        given(accountMapper.mapAccountToAccountResponse(any(Account.class))).willReturn(accountResponse);

        AccountResponse updatedAccountResponse = accountService.updateAccount(1L, accountRequest);

        assertThat(updatedAccountResponse.getAccountName()).isEqualTo("Saving");
        verify(accountRepository).save(account);
    }

    @Test
    void whenUpdateNonExistentAccount_thenThrowException() {

        given(accountRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> accountService.updateAccount(1L, accountRequest));
    }

    // ********************* TEST FOR DEPOSIT METHOD ******************************

    @Test
    void whenDeposit_thenAccountBalanceShouldIncrease() {
        BigDecimal depositAmount = BigDecimal.valueOf(500);
        BigDecimal initialBalance = account.getBalance();
        String category = "Salary";
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        Account updatedAccount = new Account();
        updatedAccount.setId(account.getId());
        updatedAccount.setCustomer(user);
        updatedAccount.setAccountName(account.getAccountName());
        updatedAccount.setAccountType(account.getAccountType());
        updatedAccount.setBalance(initialBalance.add(depositAmount));

        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            savedAccount.setBalance(initialBalance.add(depositAmount));
            return savedAccount;
        });

        Account resultAccount = accountService.deposit(1L, depositAmount, category);

        assertThat(resultAccount.getBalance()).isEqualByComparingTo(initialBalance.add(depositAmount));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void whenDepositToNonExistentAccount_thenThrowException() {

        BigDecimal depositAmount = BigDecimal.valueOf(500);
        String category = "Salary";
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.deposit(1L, depositAmount, category));
    }

    // ********************* TEST FOR WITHDRAW METHOD ******************************

    @Test
    void testWithdrawSuccess() {

        BigDecimal initialBalance = new BigDecimal("1500.00");
        BigDecimal withdrawAmount = new BigDecimal("500.00");  // Bu miktar initialBalance'dan küçük olmalıdır.
        account.setBalance(initialBalance);

        given(accountRepository.findById(any(Long.class))).willReturn(java.util.Optional.of(account));
        given(accountRepository.save(any(Account.class))).willReturn(account);

        // Act
        Account result = accountService.withdraw(1L, withdrawAmount, "Utility");

        // Assert
        verify(accountRepository).save(account);
        assertEquals(0, initialBalance.subtract(withdrawAmount).compareTo(result.getBalance()), "The balance should be reduced by the withdraw amount.");
    }


    @Test
    void testWithdrawFailureDueToInsufficientFunds() {

        BigDecimal largeWithdrawAmount = new BigDecimal("2000.00");
        given(accountRepository.findById(any(Long.class))).willReturn(java.util.Optional.of(account));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdraw(1L, largeWithdrawAmount, "Expensive Purchase");
        });

        assertEquals("Insufficient funds", exception.getMessage());
        verify(accountRepository, never()).save(any(Account.class));
    }
}
