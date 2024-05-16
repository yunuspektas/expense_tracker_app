package com.dbglobe.service;

import com.dbglobe.dto.request.LoginRequest;
import com.dbglobe.dto.response.AuthResponse;
import com.dbglobe.security.jwt.JwtUtils;
import com.dbglobe.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	public final JwtUtils jwtUtils;
	public final AuthenticationManager authenticationManager;

	public ResponseEntity<AuthResponse> authenticateUser(LoginRequest loginRequest){

		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();

		Authentication authentication =
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = "Bearer " + jwtUtils.generateJtwToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		Set<String> roles = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		Optional<String> role = roles.stream().findFirst();

		AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
		authResponse.username(userDetails.getUsername());
		authResponse.token(token.substring(7));
		authResponse.name(userDetails.getName());

		role.ifPresent(authResponse::role);

		return ResponseEntity.ok(authResponse.build());
	}
}
