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
        userMapper.insertUser(new User("U1", "관리자", "vip"));
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
    public User registerUser(String name, String grade) {
        if (!grade.equals("vip") && !grade.equals("normal")) {
            throw new IllegalArgumentException("등급은 'normal' 또는 'vip'만 가능합니다.");
        }

        int userCount = userMapper.countUsers();
        String newId = "U" + (userCount + 1);

        User newUser = new User(newId, name, grade);
        userMapper.insertUser(newUser);

        return newUser;
    }
}