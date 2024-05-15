package com.dbglobe.controller;

import com.dbglobe.dto.request.AccountRequest;
import com.dbglobe.dto.request.DepositWithDrawRequest;
import com.dbglobe.dto.response.AccountResponse;
import com.dbglobe.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<AccountResponse> createAccount(@RequestBody AccountRequest accountRequest, HttpServletRequest request) {
        return new ResponseEntity<>(accountService.saveAccount(accountRequest,request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long id, @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.deleteAccount(id, request));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<List<AccountResponse>> getAll(HttpServletRequest request){
        return ResponseEntity.ok(accountService.getAll(request));
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<String> depositMoney(@PathVariable Long accountId, @Valid @RequestBody DepositWithDrawRequest transactionRequest) {
        try {
            accountService.deposit(accountId, transactionRequest.getAmount(), transactionRequest.getCategory());
            return ResponseEntity.ok("Deposit successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{accountId}/withdraw")
    public ResponseEntity<String> withdrawMoney(@PathVariable Long accountId, @Valid @RequestBody DepositWithDrawRequest transactionRequest) {
        try {
            accountService.withdraw(accountId, transactionRequest.getAmount(), transactionRequest.getCategory());
            return ResponseEntity.ok("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }


}
