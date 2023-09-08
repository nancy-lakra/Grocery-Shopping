package com.training.grocery.order.requestmodel;

import java.util.List;
import com.training.grocery.basket.dbmodel.ProductInfo;
import com.training.grocery.order.datamodel.Status;
import com.training.grocery.order.dbmodel.DeliveryInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderHolder {

	private Long order_id;

	private Long buyer_id;

	// @Type(type = "jsonb")
	// @Basic(fetch = FetchType.LAZY)
	private DeliveryInfo del_info;

	// @Type(type = "jsonb")
	// @Basic(fetch = FetchType.LAZY)
	private List<ProductInfo> prod_list;

	private float order_amount;

	private Status order_status;

}
