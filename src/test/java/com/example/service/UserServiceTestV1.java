package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTestV1 {

    @Test
    void createUser() {
        // Mock 객체 생성
        UserRepository mockRepository = mock(UserRepository.class);

        // Stub 설정
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        // Service 생성 및 테스트
        UserService service = new UserService(mockRepository);
        User user = service.createUser("Alice", "alice@example.com");

        // 검증
        assertEquals("Alice", user.getName());

        // mockRepository 의 save(User) 가 호출되었는지 확인
        verify(mockRepository).save(any(User.class));
    }

    @Test
    void getUserById() {
        // 각 테스트마다 새로운 Mock 생성
        UserRepository mockRepository = mock(UserRepository.class);

        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Bob", "bob@example.com")));

        UserService service = new UserService(mockRepository);
        Optional<User> user = service.getUserById(1L);

        assertTrue(user.isPresent());
        assertEquals("Bob", user.get().getName());
    }
}
