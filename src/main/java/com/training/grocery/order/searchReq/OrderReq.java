package com.training.grocery.order.searchReq;

import java.time.LocalDateTime;
import java.util.List;
import com.training.grocery.genericsearch.SearchPagination;
import com.training.grocery.order.datamodel.Status;

public class OrderReq extends SearchPagination {

	private List<LocalDateTime> creationTime;
	private List<LocalDateTime> lastUpdatedTimes;
	private List<Float> amount;
	private List<Status> status;
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

	public List<LocalDateTime> getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(List<LocalDateTime> creationTime) {
		this.creationTime = creationTime;
	}

	public List<LocalDateTime> getLastUpdatedTimes() {
		return lastUpdatedTimes;
	}

	public void setLastUpdatedTimes(List<LocalDateTime> lastUpdatedTimes) {
		this.lastUpdatedTimes = lastUpdatedTimes;
	}

	public List<Float> getAmount() {
		return amount;
	}

	public void setAmount(List<Float> amount) {
		this.amount = amount;
	}

	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

	public OrderReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OrderReq(List<LocalDateTime> creationTime, List<LocalDateTime> lastUpdatedTimes, List<Float> amount,
			List<Status> status, String upperTime, String lowerTime) {
		super();
		this.creationTime = creationTime;
		this.lastUpdatedTimes = lastUpdatedTimes;
		this.amount = amount;
		this.status = status;
		this.upperTime = upperTime;
		this.lowerTime = lowerTime;
	}
}
