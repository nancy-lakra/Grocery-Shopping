package com.training.grocery.user.datamodel;

import java.time.LocalDateTime;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import com.training.grocery.user.dbmodel.Address;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;


@Entity
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(name = "user_table")
@Audited
public class DBUser {


	@Id
	@Column(name = "acc_id")
	Long id;

	@Column(name = "userid")
	String userid;

	@Column(name = "name")
	String name;

	@Column(name = "address", columnDefinition = "jsonb")
	@Type(type = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	private Address address;

	@Column(name = "email", nullable = false)
	String email;

	@Column(name = "phone", nullable = false)
	String phone;

	@Column(name = "password", nullable = false)
	String password;

	@Column(name = "role")
	String role;

	@Column(name = "is_deleted")
	public boolean isDeleted;

	@Column(name = "creation_time")
	private LocalDateTime time;

	public LocalDateTime getTime() {
		return time;
	}

	public void setTime(LocalDateTime time) {
		this.time = time;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public DBUser() {
		super();
	}

	public DBUser(Long id, String name, Address address, String email, String phone, String password, String role,
			LocalDateTime time) {
		super();
		this.id = id;
		this.name = name;
		this.userid = (new StringRandom()).generateRandomString(id, name);
		this.address = address;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.role = role;
		this.isDeleted = false;
		this.time = time;
	}

	@Override
	public String toString() {
		return String.valueOf(this.id) + " " + this.getName() + "  " + this.getEmail();
	}

}
