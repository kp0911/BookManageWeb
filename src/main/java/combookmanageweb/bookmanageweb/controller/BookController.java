package combookmanageweb.bookmanageweb.controller; // 본인 패키지에 맞게 수정

import combookmanageweb.bookmanageweb.dto.Book;
import combookmanageweb.bookmanageweb.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // JSON 형태로 데이터를 반환하는 REST API 컨트롤러
@RequestMapping("/api/books") // 이 컨트롤러의 기본 URL 주소를 설정
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // 1. 전체 도서 조회 API (기존 1번 메뉴)
    // 브라우저에서 http://localhost:8080/api/books 접속 시 실행
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    // 2. 특정 도서 검색 API (기존 2번 메뉴)
    // 브라우저에서 http://localhost:8080/api/books/B1 접속 시 B1 책 정보 반환
    @GetMapping("/{id}")
    public Book getBook(@PathVariable String id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/title/{title}")
    public List<Book> getBookByTitle(@PathVariable String title) {return bookService.getBookByTitle(title);}

    @PostMapping("/{id}/checkout")
    public Book checkout(@PathVariable String id, @RequestParam String userId) {
        return bookService.checkOutBook(id, userId);
    }
    // 예: POST http://localhost:8080/api/books/B1/checkin?userId=U1
    @PostMapping("/{id}/checkin")
    public Book checkin(@PathVariable String id, @RequestParam String userId) {
        return bookService.checkInBook(id, userId);
    }
}