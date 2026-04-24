package com.alamgir.sse.service.Implementation;

import com.alamgir.sse.dto.enums.ROLE;
import com.alamgir.sse.dto.request.LoginRequest;
import com.alamgir.sse.dto.request.RegistrationRequest;
import com.alamgir.sse.dto.response.UserResponse;
import com.alamgir.sse.entity.User;
import com.alamgir.sse.exception.IllegalStateException;
import com.alamgir.sse.repository.UserRepository;
import com.alamgir.sse.service.Abstraction.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);


    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; //  interface, not BCryptPasswordEncoder


    @Override
    public UserResponse register(RegistrationRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {throw new IllegalStateException("Email is already registered: " + request.getEmail());}

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user= User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(hashedPassword)
                .role(ROLE.FARMER) // everyone starts as FARMER
                .build();
        User savedUser=userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getEmail());
        return mapToResponse(savedUser);

    }



    @Override
    public UserResponse login(LoginRequest request){

        // 1-Check user
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new IllegalStateException("Invalid email or password"));

        // 2-Check password
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!matches) {
            logger.warn("Login failed for email: {}", request.getEmail());
            throw new IllegalStateException("Invalid email or password");
        }

        logger.info("User logged in successfully: {}", user.getEmail());
        return mapToResponse(user);
    }


    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User not found with id: " + id));
        return mapToResponse(user);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList(); // Java 16+
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalStateException("User not found with id: " + id));
        userRepository.delete(user);
    }


    private UserResponse mapToResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}


