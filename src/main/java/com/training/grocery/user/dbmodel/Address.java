package com.training.grocery.user.dbmodel;

public class Address {

	private Long street;

	private String district;

	private String state;

	private String city;

	private Long pincode;


	private String country;


	public Long getStreet() {
		return street;
	}

	public void setStreet(Long street) {
		this.street = street;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Long getPincode() {
		return pincode;
	}

	public void setPincode(Long pincode) {
		this.pincode = pincode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Address() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Address(Long street, String district, String state, String city, Long pincode, String country) {
		super();
		this.street = street;
		this.district = district;
		this.state = state;
		this.city = city;
		this.pincode = pincode;
		this.country = country;
	}


}
