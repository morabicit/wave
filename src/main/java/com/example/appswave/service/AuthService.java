package com.example.appswave.service;

import com.example.appswave.entity.User;
import com.example.appswave.enums.Role;
import com.example.appswave.exception.AuthenticationException;
import com.example.appswave.repository.UserRepository;
import com.example.appswave.authentication.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String signup(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? Role.USER : user.getRole());
        userRepository.save(user);
        return "User registered successfully!";
    }

    public Map<String, String> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            refreshTokenStore.put(refreshToken, email); // Store refresh token
            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;
        }
        throw new AuthenticationException("Invalid credentials!");
    }

    public String refresh(String refreshToken) {
        if (refreshTokenStore.containsKey(refreshToken) && jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractEmail(refreshToken);
            return jwtUtil.generateAccessToken(email);
        }
        throw new AuthenticationException("Invalid or expired refresh token!");
    }

    public void logout(String refreshToken) {
        refreshTokenStore.remove(refreshToken);
    }
}

