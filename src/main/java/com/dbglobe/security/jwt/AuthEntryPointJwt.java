package com.dbglobe.security.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Autowired
	@Qualifier("handlerExceptionResolver")
	@Lazy
	private HandlerExceptionResolver resolver;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		log.error("Unauthorized error: {}", authException.getMessage());

/*		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		final Map<String, Object> body = new HashMap<>();
		body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
		body.put("error", "Unauthorized");
		body.put("message", authException.getMessage());
		body.put("path", request.getServletPath());

		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(response.getOutputStream(), body);*/
		resolver.resolveException(request, response, null, authException);
	}
}
