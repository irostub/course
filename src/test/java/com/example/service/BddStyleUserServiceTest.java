package com.example.service;

import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BddStyleUserServiceTest {
    @Mock
    private UserRepository mockRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(mockRepository);
    }

    // ========================================
    // 전통적 Mockito 스타일
    // ========================================
    @Test
    void testCreateUserWithDuplicateEmail() {
        when(mockRepository.existsByEmail("alice@example.com"))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.createUser("Alice", "alice@example.com"));

        verify(mockRepository).existsByEmail("alice@example.com");
        verify(mockRepository, never()).save(any());
    }

    // ========================================
    // BDD 스타일
    // ========================================
    @Test
    void bddStyleUserControllerTest() {
        // Given: alice@example.com이 이미 존재하는 상황
        given(mockRepository.existsByEmail("alice@example.com"))
                .willReturn(true);

        // When: 같은 이메일로 사용자 생성 시도
        // Then: 예외가 발생하고 저장되지 않는다
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser("Alice", "alice@example.com")
        );
        assertEquals("중복된 이메일로 가입할 수 없습니다.", exception.getMessage());

        then(mockRepository).should().existsByEmail("alice@example.com");
        then(mockRepository).should(never()).save(any());
    }
}
