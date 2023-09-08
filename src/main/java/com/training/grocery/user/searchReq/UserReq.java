package com.training.grocery.user.searchReq;

import java.util.List;
import com.training.grocery.genericsearch.SearchPagination;

public class UserReq extends SearchPagination {

	private List<String> name;
	private List<String> email;
	private List<String> phone;
	private List<String> role;
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

	public List<String> getName() {
		return name;
	}

	public void setName(List<String> name) {
		this.name = name;
	}

	public List<String> getEmail() {
		return email;
	}

	public void setEmail(List<String> email) {
		this.email = email;
	}

	public List<String> getPhone() {
		return phone;
	}

	public void setPhone(List<String> phone) {
		this.phone = phone;
	}

	public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	public UserReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserReq(List<String> name, List<String> email, List<String> phone, List<String> role, String upperTime,
			String lowerTime) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.upperTime = upperTime;
		this.lowerTime = lowerTime;
	}
}
