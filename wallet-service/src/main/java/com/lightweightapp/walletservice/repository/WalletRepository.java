package com.lightweightapp.walletservice.repository;

import com.lightweightapp.walletservice.dbResource.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    Wallet findById(int walletId);

}
