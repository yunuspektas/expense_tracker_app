package com.dbglobe.service;

import com.dbglobe.domain.User;
import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceHelper {

    private final UserRepository userRepository;

    public String getUserNameFromUserDetails(UserDetails userDetails){
        String userName = userDetails.getUsername();
        getUserByUsername(userName);
        return userName;
    }
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(()->
                new ResourceNotFoundException(String.format(ErrorMessages.USER_NOT_FOUND_WITH_USERNAME, username)));
    }
}
