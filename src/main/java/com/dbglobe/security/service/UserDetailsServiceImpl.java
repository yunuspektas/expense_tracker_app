package com.dbglobe.security.service;

import com.dbglobe.domain.User;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(()->
				new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_WITH_USERNAME, username)));

		if (user != null) {
			return new UserDetailsImpl(
					user.getId(),
					user.getUsername(),
					user.getName(),
					user.getPassword(),
					user.getRole().name());
		}
		throw new UsernameNotFoundException("User '" + username+ "  ' not found");
	}
}
