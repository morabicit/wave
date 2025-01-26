package com.example.appswave.service;

import com.example.appswave.dto.UserDto;
import com.example.appswave.entity.User;
import com.example.appswave.enums.Role;
import com.example.appswave.exception.EmailAlreadyExists;
import com.example.appswave.exception.UserNotFound;
import com.example.appswave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserDto createUser(User user) {
        UserDto userDto = new UserDto();
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExists("Email already exists");
        }
        userRepository.save(user);
        userDto.setId(user.getId());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setRole(String.valueOf(user.getRole()));
        return userDto;
    }
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return UserDto.userToUserDto(users);
    }
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::userToUserDto);
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));
        if (userRepository.findByEmail(userDetails.getEmail()).isPresent() &&
                !userRepository.findByEmail(userDetails.getEmail()).get().getId().equals(id)) {
            throw new EmailAlreadyExists("Email already exists");
        }

        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }
    public UserDto partialUpdateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFound("User not found with id: " + id));
        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            if (userRepository.findByEmail(email).isPresent() &&
                    !userRepository.findByEmail(email).get().getId().equals(id)) {
                throw new EmailAlreadyExists("Email already exists");
            }
        }
        updates.forEach((key, value) -> {
            Field field = ReflectionUtils.findField(User.class, key);
            if (field != null) {
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, value);
            }
        });

        return UserDto.userToUserDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFound("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    public String assignRole(Long id, Role role) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));
        user.setRole(role);
        userRepository.save(user);
        return "Role assigned successfully";
    }
}