package com.training.grocery.payment.requestmodel;

import lombok.Data;


@Data
public class PaymentRequest {
	float amount;
	String comment;
}
