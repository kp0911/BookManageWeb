package combookmanageweb.bookmanageweb.service;

import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    // 프로그램 시작 시 초기 관리자 U1 세팅
    @PostConstruct
    public void initUser() {
        userMapper.insertUser(new User("U1", "admin", "관리자", "admin"));
        userMapper.insertUser(new User("qwe", "qwe", "qwe", "normal"));
        System.out.println(">> 초기 관리자(U1) 세팅 완료!");
    }

    // 회원 조회
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public User getOneUser(String id){
        return userMapper.findById(id);
    }


    // 회원 가입 로직
    public User registerUser(String id, String password, String name) {

        int userCount = userMapper.countUsers();

        User newUser = new User(id, name, password, "normal");
        userMapper.insertUser(newUser);

        return newUser;
    }

    public User login(String id, String password) {
        User user = userMapper.findById(id);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        return user;
    }

    public void updateUserRole(String id, String role){
        User user = userMapper.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자는 존재 하지 않습니다.");
        } else if (!(role.equals("vip") || role.equals("normal"))) {
            throw new IllegalArgumentException("허용된 권한이 아닙니다.");
        }
        userMapper.updateUserRole(id, role);
    }
} 