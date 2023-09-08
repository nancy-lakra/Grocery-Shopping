package com.training.grocery.product.datamodel;

public enum Category {
	EDIBLE("E"), CLEANING("C"), BEVERAGE("B"), SNACKS("S"), MEAT("M"), MISCELLANEOUS("MS");

	private String code;

	Category(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}

	public static Category getEnum(String s) {
		for (Category category : values()) {
			if (s.equals(category.toString()) || s.equals(String.valueOf(category)))
				return category;
		}
		return null;
	}
}
