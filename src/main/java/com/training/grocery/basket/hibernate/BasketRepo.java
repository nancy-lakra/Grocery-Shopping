package com.training.grocery.basket.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.training.grocery.basket.dbmodel.Basket;


@Repository
public interface BasketRepo extends JpaRepository<Basket, Long> {
	Basket findByUserid(String userid);
}
