package com.training.grocery.genericsearch;

public class SearchPagination {
	private int page;
	private int size;
	private String sortBy;
	private SortType sortType;

	public SortType getSortType() {
		return sortType;
	}

	public void setSortType(SortType sortType) {
		this.sortType = sortType;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public SearchPagination() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SearchPagination(int page, int size, String sortBy, SortType sortType) {
		super();
		this.page = page;
		this.size = size;
		this.sortBy = sortBy;
		this.sortType = sortType;
	}

}
