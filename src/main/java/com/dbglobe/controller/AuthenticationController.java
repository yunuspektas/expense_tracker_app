package com.dbglobe.controller;

import com.dbglobe.dto.request.LoginRequest;
import com.dbglobe.dto.response.AuthResponse;
import com.dbglobe.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/login")  // http://localhost:8080/auth/login
	public ResponseEntity<AuthResponse>authenticateUser(@RequestBody @Valid LoginRequest loginRequest){
		return authenticationService.authenticateUser(loginRequest);
	}
}
