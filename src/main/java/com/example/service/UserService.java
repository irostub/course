package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        // 비즈니스 규칙: 이메일은 반드시 @ 포함해야 함
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        // 비즈니스 규칙: 이름은 2자 이상이어야 함
        if (name == null || name.length() < 2) {
            throw new IllegalArgumentException("이름은 최소 2자 이상이어야 합니다.");
        }

        //비즈니스 규칙 : 중복된 이메일 가입은 불가능
        boolean existsEmail = userRepository.existsByEmail(email);
        if (existsEmail) {
            throw new IllegalArgumentException("중복된 이메일로 가입할 수 없습니다.");
        }

        User user = new User(name, email);
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID는 양수여야 합니다.");
        }

        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID는 양수여야 합니다.");
        }

        userRepository.deleteById(id);
    }

    public User updateUserEmail(Long id, String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
        }

        User user = userOptional.get();
        user.setEmail(newEmail);
        return userRepository.save(user);
    }
}
