package com.dbglobe.dto.request;

import javax.validation.constraints.*;

import lombok.*;

@Value
public class UserRequest {

    @NotNull(message = "Please enter your username")
    @Size(min = 4, max = 16,message = "Your username should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your username must consist of the characters .")
    String username;

    @NotNull(message = "Please enter your name")
    @Size(min = 4, max = 16,message = "Your name should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your name must consist of the characters .")
    String name;

    @NotNull(message = "Please enter your surname")
    @Size(min = 4, max = 16,message = "Your surname should be at least 4 chars")
    @Pattern(regexp = "\\A(?!\\s*\\Z).+", message = "Your surname must consist of the characters .")
    String surname;

    @NotNull(message = "Please enter your phone number")
    @Size(min = 12, max = 20,message = "Your phone number should be 12 characters long")
    String phoneNumber;

    @Email(message = "Please provide valid email")
    @NotBlank(message = "Please enter your email")
    String email;

    String password;
}
