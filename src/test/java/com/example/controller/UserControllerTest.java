package com.example.controller;

import com.example.domain.User;
import com.example.dto.Request;
import com.example.dto.Response;
import com.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService mockService;

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(mockService);
    }

    @Test
    void createUser() {
        // Given
        Request request = new Request();
        request.setParam("name", "Alice");
        request.setParam("email", "alice@example.com");

        User expectedUser = new User(1L, "Alice", "alice@example.com");
        when(mockService.createUser("Alice", "alice@example.com"))
                .thenReturn(expectedUser);

        // When
        Response response = controller.createUser(request);

        // Then
        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().contains("Alice"));
        assertTrue(response.getBody().contains("alice@example.com"));

        verify(mockService).createUser("Alice", "alice@example.com");
    }

    @Test
    void createUser_non_name() {
        // Given
        Request request = new Request();
        request.setParam("email", "alice@example.com");
        // name은 없음

        // When
        Response response = controller.createUser(request);

        // Then
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("이름은 필수"));

        // Service는 호출되지 않아야 함
        verify(mockService, never()).createUser(any(), any());
    }

    @Test
    void createUser_non_email() {
        // Given
        Request request = new Request();
        request.setParam("name", "Alice");
        // email은 없음

        // When
        Response response = controller.createUser(request);

        // Then
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("이메일은 필수"));
        verify(mockService, never()).createUser(any(), any());
    }

    @Test
    void createUser_Service_exception() {
        // Given
        Request request = new Request();
        request.setParam("name", "a");  // 너무 짧음
        request.setParam("email", "invalid");

        when(mockService.createUser("a", "invalid"))
                .thenThrow(new IllegalArgumentException("이메일 형식이 올바르지 않습니다"));

        // When
        Response response = controller.createUser(request);

        // Then
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("이메일 형식"));
    }

    @Test
    void getUser() {
        // Given
        Request request = new Request();
        request.setParam("id", "1");

        User expectedUser = new User(1L, "Alice", "alice@example.com");
        when(mockService.getUserById(1L))
                .thenReturn(Optional.of(expectedUser));

        // When
        Response response = controller.getUser(request);

        // Then
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Alice"));
        verify(mockService).getUserById(1L);
    }

    @Test
    void getUser_non_id() {
        // Given
        Request request = new Request();
        // id 파라미터 없음

        // When
        Response response = controller.getUser(request);

        // Then
        assertEquals(400, response.getStatusCode());
        verify(mockService, never()).getUserById(any());
    }
}