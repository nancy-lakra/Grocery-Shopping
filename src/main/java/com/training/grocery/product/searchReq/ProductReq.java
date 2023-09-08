package com.training.grocery.product.searchReq;

import java.util.List;
import com.training.grocery.genericsearch.SearchPagination;

public class ProductReq extends SearchPagination {

	private List<String> productName;
	private List<String> category;
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

	public List<String> getProductName() {
		return productName;
	}

	public void setProductName(List<String> productName) {
		this.productName = productName;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public ProductReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ProductReq(List<String> productName, List<String> category, String upperTime, String lowerTime) {
		super();
		this.productName = productName;
		this.category = category;
		this.upperTime = upperTime;
		this.lowerTime = lowerTime;
	}


}
