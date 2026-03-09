package combookmanageweb.bookmanageweb.service;

import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("전체 사용자 조회 테스트")
    void getAllUsers() {
        // given
        List<User> users = new ArrayList<>();
        users.add(new User("U1", "password", "홍길동", "normal"));
        given(userMapper.findAll()).willReturn(users);

        // when
        List<User> result = userService.getAllUsers();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("ID로 사용자 조회 테스트")
    void getOneUser() {
        // given
        String userId = "U1";
        User user = new User(userId, "password", "홍길동", "normal");
        given(userMapper.findById(userId)).willReturn(user);

        // when
        User result = userService.getOneUser(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void registerUser() {
        // given
        String id = "U2";
        String password = "password";
        String name = "이순신";

        // when
        User result = userService.registerUser(id, password, name);

        // then
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getRole()).isEqualTo("normal");
        verify(userMapper).insertUser(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {
        // given
        String id = "U1";
        String password = "password";
        User user = new User(id, password, "홍길동", "normal");
        given(userMapper.findById(id)).willReturn(user);

        // when
        User result = userService.login(id, password);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void login_Fail_WrongPassword() {
        // given
        String id = "U1";
        String password = "wrong_password";
        User user = new User(id, "password", "홍길동", "normal");
        given(userMapper.findById(id)).willReturn(user);

        // when & then
        assertThatThrownBy(() -> userService.login(id, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    @Test
    @DisplayName("사용자 등급 변경 성공 테스트")
    void updateUserRole_Success() {
        // given
        String id = "U1";
        String newRole = "vip";
        User user = new User(id, "password", "홍길동", "normal");
        given(userMapper.findById(id)).willReturn(user);

        // when
        userService.updateUserRole(id, newRole);

        // then
        verify(userMapper).updateUserRole(id, newRole);
    }

    @Test
    @DisplayName("사용자 등급 변경 실패 테스트 - 허용되지 않은 등급")
    void updateUserRole_Fail_InvalidRole() {
        // given
        String id = "U1";
        String newRole = "guest";
        User user = new User(id, "password", "홍길동", "normal");
        given(userMapper.findById(id)).willReturn(user);

        // when & then
        assertThatThrownBy(() -> userService.updateUserRole(id, newRole))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("허용된 권한이 아닙니다.");
    }
}
