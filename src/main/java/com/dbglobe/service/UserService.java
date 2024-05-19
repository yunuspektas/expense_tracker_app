package com.dbglobe.service;

import com.dbglobe.domain.enums.RoleType;
import com.dbglobe.dto.request.UserRequest;
import com.dbglobe.dto.response.UserResponse;
import com.dbglobe.exception.ConflictException;
import com.dbglobe.payload.mappers.UserMapper;
import com.dbglobe.payload.messages.ErrorMessages;
import com.dbglobe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponse createUser(UserRequest userRequest) {
        // !!! is username - phoneNumber unique ???
        if (userRepository.existsByUsername(userRequest.getUsername()) ||
                userRepository.existsByPhoneNumber(userRequest.getPhoneNumber()) ){
            throw new ConflictException(String.format(
                    ErrorMessages.USERNAME_PHONE_UNIQUE_MESSAGE));
        }

        var newUser = userMapper.mapUserRequestToUser(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        newUser.setRole(RoleType.CUSTOMER);

        return userMapper.mapUserToUserResponse(userRepository.save(newUser));
    }
}
