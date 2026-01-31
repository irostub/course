package com.example.service;

import com.example.domain.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VerifyBasicTest {
    @Mock
    private UserRepository mockRepository;

    @Test
    void basicVerification() {
        UserService service = new UserService(mockRepository);

        // Service 메서드 실행
        service.createUser("Alice", "alice@example.com");

        // Repository의 save()가 호출되었는지 검증
        verify(mockRepository).save(any(User.class));
    }

    @Test
    void whyVerificationNeeded() {
        // 잘못된 Service 구현
        class BrokenUserService extends UserService {
            public BrokenUserService(UserRepository repository) {
                super(repository);
            }

            @Override
            public User createUser(String name, String email) {
                // 검증만 하고 저장은 안함!
                if (name == null || name.length() < 2) {
                    throw new IllegalArgumentException("이름은 2자 이상");
                }
                // repository.save() 호출 안함!
                return new User(name, email);
            }
        }

        UserService brokenService = new BrokenUserService(mockRepository);
        User user = brokenService.createUser("Alice", "alice@example.com");

        // user 객체는 생성되었지만...
        assertNotNull(user);

        // 실제로 저장은 안되었음
        verify(mockRepository, never()).save(any(User.class));  // 검증 실패
    }

    @Test
    void verifyExactTimes() {
        UserService service = new UserService(mockRepository);

        service.createUser("Alice", "alice@example.com");
        service.createUser("Bob", "bob@example.com");
        service.createUser("Charlie", "charlie@example.com");

        // save()가 정확히 3번 호출되었는지 확인
        verify(mockRepository, times(3)).save(any(User.class));
    }

    @Test
    void verifyAtLeast() {
        UserService service = new UserService(mockRepository);

        service.createUser("Alice", "alice@example.com");
        service.createUser("Bob", "bob@example.com");

        // save()가 최소 1번 이상 호출되었는지 확인
        verify(mockRepository, atLeast(1)).save(any(User.class));

        // save()가 최소 2번 이상 호출되었는지 확인
        verify(mockRepository, atLeast(2)).save(any(User.class));

        // save()가 최소 3번 이상 호출? 실패
        //verify(mockRepository, atLeast(3)).save(any(User.class));  // 검증 실패
    }

    @Test
    void verifyAtMost() {
        UserService service = new UserService(mockRepository);

        service.createUser("Alice", "alice@example.com");

        // save()가 최대 3번 이하로 호출되었는지 확인
        verify(mockRepository, atMost(3)).save(any(User.class));

        // save()가 최대 1번 이하로 호출되었는지 확인
        verify(mockRepository, atMost(1)).save(any(User.class));
    }

    @Test
    void verifyWithMatchers() {
        UserService service = new UserService(mockRepository);

        service.createUser("Alice", "alice@example.com");

        // 모든 User 객체 허용
        verify(mockRepository).save(any(User.class));

        // null이 아닌 User 객체
        verify(mockRepository).save(notNull());

        // 특정 조건을 만족하는 User
        verify(mockRepository).save(argThat(user ->
                user.getEmail().contains("@")
        ));
    }

    @Test
    void commonMatchers() {
        // any(): 모든 값 허용
        verify(mockRepository).save(any(User.class));

        // eq(): 정확히 일치
        verify(mockRepository).findById(eq(1L));

        // anyLong(), anyString() 등: 특정 타입의 모든 값
        verify(mockRepository).findById(anyLong());

        // isNull(), isNotNull(): null 체크
        verify(mockRepository).save(isNotNull());

        // argThat(): 커스텀 조건
        verify(mockRepository).save(argThat(user ->
                user.getName().length() >= 2
        ));
    }

    /*
        ArgumentCaptor 활용
     */
    @Test
    void withoutCaptor() {
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserService service = new UserService(mockRepository);
        service.createUser("Alice", "alice@example.com");

        // save에 전달된 User의 name이 "Alice"인지 확인하고 싶은데...
        // argThat으로는 검증만 가능하고 실제 객체를 얻을 수 없음
        verify(mockRepository).save(argThat(user ->
                user.getName().equals("Alice")
        ));

        // 실제 전달된 User 객체를 얻어서 여러 속성을 검증하고 싶다면?
    }

    @Test
    void basicCaptor() {
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserService service = new UserService(mockRepository);
        service.createUser("Alice", "alice@example.com");

        // ArgumentCaptor 생성
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // save 호출 시 전달된 User를 캡처
        verify(mockRepository).save(userCaptor.capture());

        // 캡처된 User 객체 가져오기
        User capturedUser = userCaptor.getValue();

        // 상세한 검증 가능
        assertEquals("Alice", capturedUser.getName());
        assertEquals("alice@example.com", capturedUser.getEmail());
        assertNotNull(capturedUser.getId());
    }

    @Test
    void captureMultipleCalls() {
        when(mockRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    user.setId(1L);
                    return user;
                });

        UserService service = new UserService(mockRepository);
        service.createUser("Alice", "alice@example.com");
        service.createUser("Bob", "bob@example.com");
        service.createUser("Charlie", "charlie@example.com");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // 모든 호출에서 전달된 User 캡처
        verify(mockRepository, times(3)).save(userCaptor.capture());

        // 모든 값 가져오기
        List<User> capturedUsers = userCaptor.getAllValues();

        assertEquals(3, capturedUsers.size());
        assertEquals("Alice", capturedUsers.get(0).getName());
        assertEquals("Bob", capturedUsers.get(1).getName());
        assertEquals("Charlie", capturedUsers.get(2).getName());
    }
}
