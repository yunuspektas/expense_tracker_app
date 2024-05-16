package com.dbglobe.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

	String username;
	String role;
	String token;
	String name;
}
