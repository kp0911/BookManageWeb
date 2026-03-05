package combookmanageweb.bookmanageweb.controller;

import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // 이 클래스가 Spring MVC 컨트롤러임을 나타냅니다. (페이지 이동/데이터 반환 모두 가능)
@RequestMapping("/api/users") // 이 컨트롤러의 기본 URL 주소를 설정
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. 전체 회원 조회 (JSON 반환)
    // GET http://localhost:8080/api/users
    @GetMapping
    @ResponseBody // 이 메서드는 View가 아닌 JSON 데이터를 반환합니다.
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 2. 특정 회원 조회 (JSON 반환)
    @GetMapping("/{id}")
    @ResponseBody // 이 메서드는 View가 아닌 JSON 데이터를 반환합니다.
    public User getUser(@PathVariable String id){
        return userService.getOneUser(id);
    }

    // 3. 회원 가입 (JSON 반환)
    @PostMapping("/register")
    @ResponseBody
    public User registerUser(@RequestParam String id, @RequestParam String name, @RequestParam String password) {
        return userService.registerUser(id, password, name);
    }
    
    // 4. 로그인 처리 (페이지 리다이렉트)
    @PostMapping("/login")
    public String login(@RequestParam String id, @RequestParam String password, HttpServletRequest request, Model model) {
        try {
            User loginUser = userService.login(id, password);
            
            // 세션이 있으면 기존 세션 반환, 없으면 신규 생성
            HttpSession session = request.getSession();

            // 세션에 로그인 회원 정보 보관
            session.setAttribute("loginUser", loginUser);

            return "redirect:/"; // 메인 페이지로 리다이렉트
        } catch (IllegalArgumentException e) {
            return "redirect:/login?error"; // 로그인 실패 시 login 페이지로 리다이렉트하며 에러 파라미터 전달
        }
    }

    // 5. 로그아웃 처리 (페이지 리다이렉트)
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // false 파라미터: 세션이 없으면 null을 반환 (굳이 새로 만들지 않음)
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // 세션 정보 삭제
        }

        return "redirect:/";
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<String> upgradeUserRole(@PathVariable String id, @RequestParam String newRole) {
        userService.updateUserRole(id, newRole);
        return ResponseEntity.ok("등급 변경 완료");
    }
}
