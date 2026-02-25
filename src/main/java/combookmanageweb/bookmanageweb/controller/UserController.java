package combookmanageweb.bookmanageweb.controller;

import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // JSON 형태로 데이터를 반환하는 REST API 컨트롤러
@RequestMapping("/api/users") // 이 컨트롤러의 기본 URL 주소를 설정
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. 전체 회원 조회 (기존 5번 메뉴)
    // GET http://localhost:8080/api/users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id){
        return userService.getOneUser(id);
    }

    // 예: POST http://localhost:8080/api/users/register?name=홍길동&grade=normal
    @PostMapping("/register")
    public User registerUser(@RequestParam String name, @RequestParam String grade) {
        return userService.registerUser(name, grade);
    }
}
