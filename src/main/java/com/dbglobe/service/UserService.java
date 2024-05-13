package com.dbglobe.service;

import com.dbglobe.domain.User;
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
    private final UserRoleService userRoleService;
    private final UserMapper userMapper;

    public UserResponse saveUser(UserRequest userRequest) {
        // !!! is username - phoneNumber unique ???
        if (userRepository.existsByUsername(userRequest.getUsername()) ||
                userRepository.existsByPhoneNumber(userRequest.getPhoneNumber()) ){
            throw new ConflictException(String.format(
                    ErrorMessages.USERNAME_PHONE_UNIQUE_MESSAGE));
        }

        User newUser = userMapper.mapUserRequestToUser(userRequest);
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        if(userRequest.getUsername().equals("Admin")) {
            newUser.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
            newUser.setBuilt_in(Boolean.TRUE);
        } else {
            newUser.setUserRole(userRoleService.getUserRole(RoleType.CUSTOMER));
            newUser.setBuilt_in(Boolean.FALSE);
        }
        return userMapper.mapUserToUserResponse( userRepository.save(newUser));
    }

    public int countAdminOrCustomer(RoleType roleType) {
        return userRepository.countAdminOrCustomer(roleType);
    }
}
