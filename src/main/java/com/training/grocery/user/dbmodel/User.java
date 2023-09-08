package com.training.grocery.user.dbmodel;


import java.time.LocalDateTime;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import com.training.grocery.user.datamodel.Role;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;


@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
public class User {


	Long id;

	String name;

	@Type(type = "jsonb")
	private Address address;

	String email;

	String phone;

	String password;

	Role role;

	private boolean isDeleted;

	private LocalDateTime time;


	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
	}

	public User() {
		super();
	}

	public User(Long id, String name, Address address, String email, String phone, String password, Role role) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.role = role;
		this.isDeleted = false;
		this.time = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return String.valueOf(this.id) + " " + this.getName() + "  " + this.getEmail();
	}

}
