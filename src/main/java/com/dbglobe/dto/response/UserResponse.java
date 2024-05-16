package com.dbglobe.dto.response;

import lombok.*;

@Value
@Builder
public class UserResponse {

    Long userId;
    String username;
    String userRole;
}
