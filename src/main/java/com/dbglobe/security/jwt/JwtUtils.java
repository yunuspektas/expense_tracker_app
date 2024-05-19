package com.dbglobe.security.jwt;

import com.dbglobe.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

	@Value("${backendapi.app.jwtExpirationMs}")
	private long jwtExpirationMs;

	@Value("${backendapi.app.jwtSecret}")
	private String jwtSecret;

	// Not: Generate JWT *************************************************
	public String generateJtwToken(Authentication authentication) {

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		return generateTokenFromUsername(userDetails.getUsername());
	}
	/**
	 * @param username as String
	 * @return JWT signed with algorithm and our jwtSecret key
	 */
	public String generateTokenFromUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	// Not: Validate JWT *************************************************
	public boolean validateJwtToken(String jwtToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwtToken);
			return true;
		} catch (ExpiredJwtException e) {
			log.error("Jwt token is expired : {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			log.error("Jwt token is unsupported : {}", e.getMessage());
		} catch (MalformedJwtException e) {
			log.error("Jwt token is invalid : {}", e.getMessage());
		} catch (SignatureException e) {
			log.error("Jwt Signature is invalid : {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			log.error("Jwt is empty : {}", e.getMessage());
		}
		return false;
	}


	// Not: getUsernameForJWT ********************************************
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}
