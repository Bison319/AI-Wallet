package com.aiwalletplatform.transaction.repository;

import com.aiwalletplatform.commons.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Transaction Repository
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findByWalletId(String walletId, Pageable pageable);
    Page<Transaction> findByUserId(String userId, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.walletId = ?1 AND t.createdAt BETWEEN ?2 AND ?3")
    List<Transaction> findTransactionsByDateRange(String walletId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.walletId = ?1")
    Long getTotalTransactionAmount(String walletId);
}
