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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StubbingBasicTest {

    @Mock
    private UserRepository mockRepository;

    @Test
    void basicStubbing() {
        // Given: Mock 동작 정의
        User expectedUser = new User(1L, "Alice", "alice@example.com");

        when(mockRepository.findById(1L))
                .thenReturn(Optional.of(expectedUser));

        // When: Mock 메서드 호출
        Optional<User> result = mockRepository.findById(1L);

        // Then: Stubbing된 값이 반환됨
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
        assertEquals("alice@example.com", result.get().getEmail());
    }

    @Test
    void multipleReturnValues() {
        // 같은 메서드를 여러 번 호출할 때 다른 값 반환
        when(mockRepository.findById(1L))
                .thenReturn(
                        Optional.of(new User(1L, "Alice", "alice@example.com")),
                        Optional.of(new User(1L, "Alice Updated", "alice@example.com")),
                        Optional.empty()
                );

        // 첫 번째 호출
        Optional<User> first = mockRepository.findById(1L);
        assertEquals("Alice", first.get().getName());

        // 두 번째 호출
        Optional<User> second = mockRepository.findById(1L);
        assertEquals("Alice Updated", second.get().getName());

        // 세 번째 호출
        Optional<User> third = mockRepository.findById(1L);
        assertTrue(third.isEmpty());

        // 네 번째 호출부터는 마지막 값 반복
        Optional<User> fourth = mockRepository.findById(1L);
        assertTrue(fourth.isEmpty());
    }

    @Test
    void stubbingWithMatchers() {
        // 모든 User 객체에 대해 동작
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);  // ID만 설정
                    return user;
                });

        User alice = mockRepository.save(new User("Alice", "alice@example.com"));
        assertEquals(1L, alice.getId());
        assertEquals("Alice", alice.getName());

        User bob = mockRepository.save(new User("Bob", "bob@example.com"));
        assertEquals(1L, bob.getId());
        assertEquals("Bob", bob.getName());
    }

    @Test
    void stubbingWithSpecificMatchers() {
        // 1부터 10 사이의 ID에 대해서만 동작
        when(mockRepository.findById(longThat(id -> id >= 1 && id <= 10)))
                .thenReturn(Optional.of(new User(1L, "Valid User", "valid@example.com")));

        // ID가 범위 안에 있으면 User 반환
        assertTrue(mockRepository.findById(5L).isPresent());

        // ID가 범위 밖이면 기본값(empty) 반환
        assertTrue(mockRepository.findById(100L).isEmpty());
    }

    @Test
    void throwException() {
        // findById 호출 시 예외 발생
        when(mockRepository.findById(999L))
                .thenThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 예외가 발생하는지 확인
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> mockRepository.findById(999L)
        );

        assertEquals("사용자를 찾을 수 없습니다", exception.getMessage());
    }

    @Test
    void throwExceptionClass() {
        // 예외 객체 대신 클래스만 지정 가능
        when(mockRepository.findById(anyLong()))
                .thenThrow(RuntimeException.class);

        assertThrows(
                RuntimeException.class,
                () -> mockRepository.findById(1L)
        );
    }

}
