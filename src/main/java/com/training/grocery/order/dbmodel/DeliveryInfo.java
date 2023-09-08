package com.training.grocery.order.dbmodel;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import org.hibernate.annotations.Type;
import com.training.grocery.user.dbmodel.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeliveryInfo {

	@Column(columnDefinition = "jsonb")
	@Type(type = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private Address address;

	@Column
	String phone_no;

	@Column
	LocalDateTime delivery_date_time;

}
