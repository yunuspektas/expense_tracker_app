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
		//!!! Gelen requestin icinden kullanici adi ve parola bilgisi aliniyor
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();

		// !!! authenticationManager uzerinden kullaniciyi valide ediyoruz
		Authentication authentication =
				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));

		// !!! valide edilen kullanici Context e atiliyor
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// !!! JWT token olusturuluyor
		String token = "Bearer " + jwtUtils.generateJtwToken(authentication);
		// !!! GrantedAuthority turundeki role yapisini String turune ceviriliyor
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		Set<String> roles = userDetails.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toSet());

		//!!! bir kullanicinin birden fazla rolu olmayacagi icin ilk indexli elemani aliyoruz
		Optional<String> role = roles.stream().findFirst();

		// another way of using builder
		AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
		authResponse.username(userDetails.getUsername());
		authResponse.token(token.substring(7));
		authResponse.name(userDetails.getName());
		// !!! role bilgisi varsa response nesnesindeki degisken setleniyor
		role.ifPresent(authResponse::role);

		// !!! AuthResponse nesnesi ResponseEntity ile donduruyoruz
		return ResponseEntity.ok(authResponse.build());
	}
}
