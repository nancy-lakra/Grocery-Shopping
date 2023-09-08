package com.training.grocery.payment.datamodel;


public enum PaymentType {
	PAID_FOR_ORDER("P"), REFUND("R"), TOPUP("T"), WITHDRAW("W");

	private String code;

	PaymentType(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}
