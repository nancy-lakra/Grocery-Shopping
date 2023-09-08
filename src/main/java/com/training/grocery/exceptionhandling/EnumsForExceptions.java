package com.training.grocery.exceptionhandling;

public enum EnumsForExceptions {

	Bad_Request("Incorrect Details"),
	Product_Not_In_Basket("Product not found in basket"),
	Product_Unavailable("Product unavailable"),
	Basket_Empty("Basket Empty"),
	User_Exists("The user already exists"),
	User_Id_missing("UserId is missing"),
	User_Id_mismatch("UserId mismatch between token and request"),
	User_Not_Found("User not found"),
	Order_Not_In_Records("Order not found in records"),
	Not_Authorized("User not authorized"),
	Order_Exists("Order already placed"),
	Bad_Credentials("Bad user credentials"),
	Invalid_Amount("Invalid amount in request"),
	Insufficient_Balance("Insufficient balance"),
	Invalid_Value_Product("Invalid values for either price, stock, or discount"),
	Invalid_Product_Request("Invalid request to add product"),
	ProductId_null("ProductId is null"),
	User_Not_Seller("The user is not a seller"),
	Product_Not_Found("Product not found"),
	Product_Found("Product already exists"),
	Deleted_User_Exists("This user has been deleted"),
	Deleted_Product_Exists("This product has been deleted"),
	Request_already_exists("This request aleady exists"),
	Order_already_cancelled("This order has been cancelled already"),
	Order_already_delivered("this order has been already delivered"),
	Order_already_returned("this order has been already returned"),
	Order_still_not_delivered("this order has not been delivered yet"),
	duration_for_return_expired("cannot return order after duration expiration"),
	User_Not_Admin("The user is not an admin");

	String code;

	EnumsForExceptions(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}
