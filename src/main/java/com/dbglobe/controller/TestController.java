package com.dbglobe.controller;

import com.dbglobe.exception.ResourceNotFoundException;
import com.dbglobe.exception.UnauthorizedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test-unauthorized")
    public ResponseEntity<String> testUnauthorized() {
        throw new UnauthorizedException("Test Unauthorized Exception");
    }

    @GetMapping("/test-resourcenotfound")
    public ResponseEntity<String> testAccessDenied() {
        throw new ResourceNotFoundException("Test Access Denied Exception");
    }
}
