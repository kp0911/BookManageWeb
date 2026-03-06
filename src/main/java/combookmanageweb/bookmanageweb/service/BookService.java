package combookmanageweb.bookmanageweb.service;

import combookmanageweb.bookmanageweb.dto.Book;
import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.mapper.BookMapper;
import combookmanageweb.bookmanageweb.mapper.UserMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service // 스프링에게 "이게 비즈니스 로직을 처리하는 서비스 계층이야!" 라고 알려줍니다.
@RequiredArgsConstructor // final이 붙은 필드(BookMapper)를 자동으로 주입(DI)해 줍니다.
public class BookService {

    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    // 1. 초기 더미 데이터 30권 세팅 (서버 시작 시 자동 실행)
    @PostConstruct
    public void initData() {
        for (int i = 1; i <= 20; i++) {
            bookMapper.insertBook(new Book("B" + i, "일반", "일반도서" + i, true, false, null, null));
        }
        for (int i = 21; i <= 30; i++) {
            // 참고자료는 rentable(대출가능)을 false로 세팅
            bookMapper.insertBook(new Book("B" + i, "참고자료", "백과사전" + i, false, false, null, null));
        }
        System.out.println(">> DB 초기 도서 30권 세팅 완료!");
    }

    // 2. 전체 도서 조회
    public List<Book> getAllBooks() {
        return bookMapper.findAll();
    }

    // 허용된 도서 정보 조회
    public List<Book> getAvailableBooks() {
        return bookMapper.getAvailableBooks();
    }

    // 로그인 한 사용자의 대출 도서 목록 조회
    public List<Book> getRentedBooks(String userId) {
        return bookMapper.getRentedBooks(userId);
    }

    // 3. 단건 도서 검색 및 가드 클로즈 (기존 getBookOrThrow 역할)
    public Book getBookById(String id) {
        Book book = bookMapper.findById(id);
        if (book == null) {
            // 기존에 만든 BookNotFoundException이 있다면 그걸 써도 됩니다.
            throw new IllegalArgumentException("해당 번호의 도서는 존재하지 않습니다.");
        }
        return book;
    }

    public List<Book> getBookByTitle(String title){
        List<Book> book = bookMapper.findByTitle(title);
        if (book == null) {
            // 기존에 만든 BookNotFoundException이 있다면 그걸 써도 됩니다.
            throw new IllegalArgumentException("해당 번호의 도서는 존재하지 않습니다.");
        }
        return book;
    }

    public Book checkOutBook(String bookId, String userId){
        Book targetBook = bookMapper.findById(bookId);
        if (!targetBook.isRentable()) {
            throw new IllegalArgumentException("해당 도서는 열람실 전용 참고자료이므로 대출할 수 없습니다.");
        }

        if (targetBook.isRented()) {
            throw new IllegalArgumentException("해당 제목의 책은 이미 대출되어 있습니다.");
        }

        User targetUser = userMapper.findById(userId);
        if (targetUser == null) {
            throw new IllegalArgumentException("해당 사용자는 존재하지 않습니다.");
        }

        int overdueCount = bookMapper.countOverdueBooks(userId);
        if (overdueCount > 0) {
            throw new IllegalArgumentException("현재 연체 중인 도서가 " + overdueCount + "권 있습니다. 모두 반납하기 전까지 새로운 대출이 불가합니다.");
        }

        targetBook.setRented(true);
        targetBook.setReturnDate(LocalDate.now().plusDays(targetUser.getRentalDays()));
        //System.out.println("날짜: "+LocalDate.now().plusDays(targetUser.getRentalDays()));
        targetBook.setUserId(userId);
        bookMapper.updateBook(targetBook); // 변경된 상태를 DB에 덮어쓰기

        return targetBook;
    }

    public Book checkInBook(String bookId, String userId) {
        Book targetBook = getBookById(bookId);

        if (!targetBook.isRented()) {
            throw new IllegalArgumentException("해당 제목의 책은 이미 반납되어 있습니다.");
        }

        User targetUser = userMapper.findById(userId);
        if (targetUser == null) {
            throw new IllegalArgumentException("해당 사용자는 존재하지 않습니다.");
        }

        if (targetBook.getUserId() == null || !targetBook.getUserId().equals(userId)) {
            throw new IllegalArgumentException("해당 도서는 [" + targetUser.getName() + "] 님이 대출한 도서가 아닙니다.");
        }

        // 모든 검증 통과 -> 상태 초기화 및 DB 업데이트
        targetBook.setRented(false);
        targetBook.setReturnDate(null);
        targetBook.setUserId(null); // 대출자 기록 삭제

        bookMapper.updateBook(targetBook);
        return targetBook;
    }
}
