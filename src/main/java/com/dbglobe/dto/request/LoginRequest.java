package com.dbglobe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class LoginRequest {

	@NotNull(message = "Username must not be empty")
	String username;

	@NotNull(message = "Password must not be empty")
	String password;

}
