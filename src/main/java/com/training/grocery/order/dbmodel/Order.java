package com.training.grocery.order.dbmodel;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import com.training.grocery.basket.dbmodel.ProductInfo;
import com.training.grocery.order.datamodel.Status;
import com.training.grocery.order.requestmodel.OrderHolder;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(name = "grocery_orders")
@Audited
public class Order {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long order_id;

	@Column
	private Long buyer_id;

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private DeliveryInfo del_info;

	@Column
	LocalDateTime order_date_time;

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private List<ProductInfo> prod_list;

	@Column
	private float order_amount;

	@Column
	private Status order_status;

	@Column
	private LocalDateTime last_processed;

	public Order(OrderHolder order) {
		this.order_id = order.getOrder_id();
		this.buyer_id = order.getBuyer_id();
		this.del_info = order.getDel_info();
		this.order_date_time = LocalDateTime.now();
		this.prod_list = order.getProd_list();
		this.order_amount = order.getOrder_amount();
		this.order_status = Status.ORDER_PLACED;
		this.last_processed = LocalDateTime.now();
	}
}
