package com.dbglobe.payload.messages;

public class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String ROLE_NOT_FOUND = "There is no role like that, check the database";
    public static final String ROLE_ALREADY_EXIST = "Role already exist in DB";
    public static final String USERNAME_PHONE_UNIQUE_MESSAGE = "Username or Phone Number must be unique";

}
