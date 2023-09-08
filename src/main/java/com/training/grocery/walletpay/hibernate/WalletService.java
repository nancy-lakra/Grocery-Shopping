package com.training.grocery.walletpay.hibernate;

import com.training.grocery.walletpay.dbmodel.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class WalletService {

	@Autowired
	WalletRepository repo;

	public float getAmount(String userid) {
		Wallet temp = repo.findByUserid(userid);
		return temp.getAmount();
	}

	public Long create(String userid) {
		Wallet temp = Wallet.builder().userid(userid).amount(0).build();
		repo.save(temp);
		Long id = repo.findByUserid(userid).getId();
		if (id != null)
			return id;
		return null;
	}


	public void updateAmount(String userid, float amount) {
		Wallet update = repo.findByUserid(userid);
		update.setAmount(update.getAmount() + amount);
		repo.save(update);
	}

	public void delete(String userid) {
		repo.delete(repo.findByUserid(userid));// .orElseThrow());
	}
}
