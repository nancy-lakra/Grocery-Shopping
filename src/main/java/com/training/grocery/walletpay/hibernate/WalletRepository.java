package com.training.grocery.walletpay.hibernate;

import com.training.grocery.walletpay.dbmodel.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

	Wallet findByUserid(String userid);
}
