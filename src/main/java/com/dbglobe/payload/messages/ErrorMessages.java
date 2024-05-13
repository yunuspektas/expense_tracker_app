package com.dbglobe.payload.messages;

public class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String ROLE_NOT_FOUND = "There is no role like that, check the database";
    public static final String ACCOUNT_NOT_FOUND_WITH_ID = "Account not found with id : %s";
    public static final String ROLE_ALREADY_EXIST = "Role already exist in DB";
    public static final String USERNAME_PHONE_UNIQUE_MESSAGE = "Username or Phone Number must be unique";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username : %s";
    public static final String TRANSACTION_NOT_FOUND_WITH_ID = "Transaction not found with id : %s";
    public static final String INSUFFICIENT_AMOUNT_MESSAGE = "Insufficient funds";
    public static final String WRONG_ACCOUNT_MESSAGE = "You have no permission to delete other's accounts";

}
