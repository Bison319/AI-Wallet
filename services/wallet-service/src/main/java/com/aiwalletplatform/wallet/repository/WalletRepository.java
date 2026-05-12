package com.aiwalletplatform.wallet.repository;

import com.aiwalletplatform.commons.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Wallet Repository
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByWalletNumber(String walletNumber);
    Optional<Wallet> findByUserId(String userId);
    
    @Query("SELECT COUNT(w) FROM Wallet w WHERE w.userId = ?1")
    int countByUserId(String userId);
}
