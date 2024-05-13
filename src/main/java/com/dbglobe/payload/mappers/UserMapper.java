package com.dbglobe.payload.mappers;

import com.dbglobe.domain.User;
import com.dbglobe.dto.request.UserRequest;
import com.dbglobe.dto.response.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse mapUserToUserResponse(User user) {

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .userRole(user.getUserRole().getRoleType().name())
                .build();
    }

    public User mapUserRequestToUser(UserRequest userRequest) {

        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .surname(userRequest.getSurname())
                .password(userRequest.getPassword())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .build();
    }
}
