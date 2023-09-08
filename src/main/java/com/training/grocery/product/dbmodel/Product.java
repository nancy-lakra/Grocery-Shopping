package com.training.grocery.product.dbmodel;

import java.time.LocalDateTime;
import com.training.grocery.product.datamodel.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Product {

	private Long id;

	private String productId;

	private String name;

	private Category category;

	private float price;

	private float discount;

	private Long stock;

	private String userid;

	private String description;

	LocalDateTime time;

	public boolean isDeleted;

	public Product(String name, Category category, float price, float discount, Long stock, String userid,
			String description) {
		super();
		this.name = name.toLowerCase();
		this.category = category;
		this.price = price;
		this.discount = discount;
		this.stock = stock;
		this.userid = userid;
		this.description = description;
		this.time = LocalDateTime.now();
		this.isDeleted = false;
	}


}
