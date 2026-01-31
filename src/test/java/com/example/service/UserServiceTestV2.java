package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTestV2 {

    @Mock  // Mock 객체로 선언
    private UserRepository mockRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Mock 어노테이션을 활성화
        MockitoAnnotations.openMocks(this);

        // Service 생성
        userService = new UserService(mockRepository);
    }

    @Test
    void createUser() {
        // Mock은 이미 생성되어 있음
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        User user = userService.createUser("Alice", "alice@example.com");

        assertEquals("Alice", user.getName());
        verify(mockRepository).save(any(User.class));
    }

    @Test
    void getUserById() {
        // mockRepository는 각 테스트마다 새로 초기화됨
        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Bob", "bob@example.com")));

        Optional<User> user = userService.getUserById(1L);

        assertTrue(user.isPresent());
        assertEquals("Bob", user.get().getName());
    }
}
