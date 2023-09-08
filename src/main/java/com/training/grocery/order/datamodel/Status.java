package com.training.grocery.order.datamodel;

import lombok.Getter;


@Getter
public enum Status {

	ORDER_PLACED("OP"),
	DELIVERED("DEL"),
	RETURNED("RET"),
	RETURN_REQUESTED("RET_REQ"),
	CANCELLED("CAN"),
	CANCEL_REQUEST("CAN_REQ"),
	REQUEST_BEING_CONSIDERED("REQ_CON"),
	OUT_FOR_DELIVERY("OUT_D");

	private String code;

	Status(String code) {
		this.code = code;
	}

	public String getStatusChar() {
		String ret = this.code;
		return ret;
	}
}
