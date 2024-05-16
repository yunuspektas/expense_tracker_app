package com.dbglobe.controller;

import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.request.DepositWithDrawRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest accountRequest, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(accountService.createAccount(accountRequest,userDetails), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id,
                                                         @RequestBody AccountRequest accountRequest,
                                                         @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountRequest, userDetails));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public void deleteAccount(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        accountService.deleteAccount(id, userDetails);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<List<AccountResponse>> getAll(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(accountService.getAll(userDetails));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> depositMoney(@PathVariable Long accountId,
                                               @Valid @RequestBody DepositWithDrawRequest transactionRequest,
                                               @AuthenticationPrincipal UserDetails userDetails) {

            accountService.deposit(accountId, userDetails, transactionRequest.getAmount(), transactionRequest.getCategory());
            return ResponseEntity.ok("Deposit successful.");
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdrawMoney(@PathVariable Long accountId,
                                                @Valid @RequestBody DepositWithDrawRequest transactionRequest,
                                                @AuthenticationPrincipal UserDetails userDetails) {

            accountService.withdraw(accountId, userDetails, transactionRequest.getAmount(), transactionRequest.getCategory());
            return ResponseEntity.ok("Withdrawal successful.");
    }
}
