package com.training.grocery.product.datamodel;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "products")
@Audited
@Getter
@Setter
@NoArgsConstructor
public class DBProduct {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "name")
	private String name;

	@Column(name = "category")
	private String category;

	@Column(name = "price")
	private float price;

	@Column(name = "discount")
	private float discount;

	@Column(name = "stock")
	private Long stock;

	@Column(name = "userid")
	private String userid;

	@Column(name = "description")
	private String description;

	@Column(name = "creation_time")
	LocalDateTime time;

	@Column(name = "is_deleted")
	public boolean isDeleted;

	public DBProduct(String name, String category, float price, float discount, Long stock, String userid,
			String description, LocalDateTime time) {
		super();
		this.name = name.toLowerCase();
		this.category = category;
		this.price = price;
		this.discount = discount;
		this.stock = stock;
		this.userid = userid;
		this.description = description;
		this.time = time;
		this.isDeleted = false;
	}


}
