package com.training.grocery.payment.searchReq;

import java.util.List;
import com.training.grocery.genericsearch.SearchPagination;
import com.training.grocery.payment.datamodel.PaymentStatus;
import com.training.grocery.payment.datamodel.PaymentType;

public class PaymentReq extends SearchPagination {

	private List<Long> orderId;
	private List<Float> amount;
	private List<PaymentType> type;
	private List<PaymentStatus> status;
	private String upperTime;
	private String lowerTime;

	public String getUpperTime() {
		return upperTime;
	}

	public void setUpperTime(String upperTime) {
		this.upperTime = upperTime;
	}

	public String getLowerTime() {
		return lowerTime;
	}

	public void setLowerTime(String lowerTime) {
		this.lowerTime = lowerTime;
	}

	public List<Long> getOrderId() {
		return orderId;
	}

	public void setOrderId(List<Long> orderId) {
		this.orderId = orderId;
	}

	public List<Float> getAmount() {
		return amount;
	}

	public void setAmount(List<Float> amount) {
		this.amount = amount;
	}

	public List<PaymentType> getType() {
		return type;
	}

	public void setType(List<PaymentType> type) {
		this.type = type;
	}

	public List<PaymentStatus> getStatus() {
		return status;
	}

	public void setStatus(List<PaymentStatus> status) {
		this.status = status;
	}

	public PaymentReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PaymentReq(List<Long> orderId, List<Float> amount, List<PaymentType> type, List<PaymentStatus> status,
			String upperTime, String lowerTime) {
		super();
		this.orderId = orderId;
		this.amount = amount;
		this.type = type;
		this.status = status;
		this.upperTime = upperTime;
		this.lowerTime = lowerTime;
	}
}
