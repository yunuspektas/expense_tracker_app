package com.dbglobe.controller;

import com.dbglobe.domain.Transaction;
import com.dbglobe.dto.request.TransactionRequest;
import com.dbglobe.dto.response.TransactionResponse;
import com.dbglobe.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping
public class TransactionController {
    @Autowired
    private TransactionService transactionService;



    @GetMapping("/accounts/{accountId}/transactions")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable Long accountId,
                                                                              @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(transactionService.getAllTransactionsByAccount(accountId, userDetails), HttpStatus.OK);
    }

    @DeleteMapping("/transactions/{transactionId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public void deleteTransaction(@PathVariable Long transactionId,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        transactionService.deleteTransaction(transactionId, userDetails);
    }

    @GetMapping("/transaction/search")
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(@RequestParam String category,
                                                                        @RequestParam Long accountId,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                        @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(transactionService.searchTransactions(accountId, category, startDate, endDate, userDetails));
    }
}
