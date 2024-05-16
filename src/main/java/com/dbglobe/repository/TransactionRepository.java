package com.dbglobe.repository;

import com.dbglobe.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

   // List<Transaction> findByCategoryContainingAndDateBetween(String category, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByAccount_Customer_IdAndAccount_IdAndCategoryContainingAndDateBetween(
            Long customerId, Long accountId, String category, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByAccountId(Long accountId);

}
