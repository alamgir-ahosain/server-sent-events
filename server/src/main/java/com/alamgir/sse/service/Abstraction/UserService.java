package com.alamgir.sse.service.Abstraction;

import com.alamgir.sse.dto.request.LoginRequest;
import com.alamgir.sse.dto.request.RegistrationRequest;
import com.alamgir.sse.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse register(RegistrationRequest request);
    UserResponse login(LoginRequest request);
    UserResponse getUserById(String id);
    List<UserResponse> getAllUsers();
    void deleteUser(String id);
}
