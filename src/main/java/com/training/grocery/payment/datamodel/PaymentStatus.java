package com.training.grocery.payment.datamodel;


public enum PaymentStatus {
	SUCCESS("S"), FAILED("F");

	public String code;

	PaymentStatus(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}
