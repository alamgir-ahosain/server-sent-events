package com.alamgir.sse.controller;


import com.alamgir.sse.dto.request.LoginRequest;
import com.alamgir.sse.dto.request.RegistrationRequest;
import com.alamgir.sse.dto.response.UserResponse;
import com.alamgir.sse.service.Abstraction.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    Logger logger= LoggerFactory.getLogger(UserController.class);
    private  final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistrationRequest request) {
        logger.info("UserController: POST /api/user/register called");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("UserController: POST /api/user/login called");
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        logger.info("UserController: GET /api/user/{id} called");
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<UserResponse>> getAllUsers() {
        logger.info("UserController: GET /api/user/all called");
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        logger.info("UserController: DELETE /api/user/{id} called");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

