package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)  // 이 한 줄로 Mock 자동 초기화
public class UserServiceTestV3 {

    @Mock
    private UserRepository mockRepository;

    @Test
    void createUser() {
        // @BeforeEach 없이도 mockRepository가 자동으로 초기화됨
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserService service = new UserService(mockRepository);
        User user = service.createUser("Alice", "alice@example.com");

        assertEquals("Alice", user.getName());
        verify(mockRepository).save(any(User.class));
    }

    @Test
    void getUserById() {
        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(new User(1L, "Bob", "bob@example.com")));

        UserService service = new UserService(mockRepository);
        Optional<User> user = service.getUserById(1L);

        assertTrue(user.isPresent());
        assertEquals("Bob", user.get().getName());
    }

    @Test
    void getUserByIdWithWrongId() {
        UserService service = new UserService(mockRepository);

        assertThrows(IllegalArgumentException.class, () -> {
            service.getUserById(-1L);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            service.getUserById(null);
        });
    }


    //getAllUsers
    //deleteUser
    //updateUserEmail
}