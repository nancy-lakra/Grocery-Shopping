package com.training.grocery.product.requestmodel;

import com.training.grocery.product.datamodel.Category;

public class ByNameCategoryRequest {
	private String name;
	private Category category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public ByNameCategoryRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ByNameCategoryRequest(String name, Category category) {
		super();
		this.name = name;
		this.category = category;
	}

}
