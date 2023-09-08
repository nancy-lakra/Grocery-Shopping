package com.training.grocery.user.datamodel;

public enum Role {
	ADMIN("AD"), BUYER("BY"), SELLER("SL");

	private String code;

	Role(String role) {
		this.code = role;
	}

	@Override
	public String toString() {
		return code;
	}

	public static Role getEnum(String s) {
		for (Role role : values()) {
			if (s.equals(role.toString()) || s.equals(String.valueOf(role)))
				return role;
		}
		return null;
	}
}
