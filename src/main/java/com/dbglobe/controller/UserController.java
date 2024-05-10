package com.dbglobe.controller;

import com.dbglobe.dto.request.UserRequest;
import com.dbglobe.dto.response.UserResponse;
import com.dbglobe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Not : saveUser() *************************************************************
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<UserResponse> saveUser(
            @RequestBody @Valid UserRequest userRequest){
        return ResponseEntity.ok(userService.saveUser(userRequest));
    }


}
