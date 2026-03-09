package combookmanageweb.bookmanageweb.service;

import combookmanageweb.bookmanageweb.dto.Book;
import combookmanageweb.bookmanageweb.dto.User;
import combookmanageweb.bookmanageweb.mapper.BookMapper;
import combookmanageweb.bookmanageweb.mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookMapper bookMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("전체 도서 조회 테스트")
    void getAllBooks() {
        // given
        List<Book> books = new ArrayList<>();
        books.add(new Book("B1", "일반", "테스트책1", true, false, null, null));
        given(bookMapper.findAll()).willReturn(books);

        // when
        List<Book> result = bookService.getAllBooks();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("테스트책1");
    }

    @Test
    @DisplayName("대출 가능 도서 조회 테스트")
    void getAvailableBooks() {
        // given
        List<Book> books = new ArrayList<>();
        books.add(new Book("B1", "일반", "테스트책1", true, false, null, null));
        given(bookMapper.getAvailableBooks()).willReturn(books);

        // when
        List<Book> result = bookService.getAvailableBooks();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("사용자 대출 목록 조회 테스트")
    void getRentedBooks() {
        // given
        String userId = "U1";
        List<Book> books = new ArrayList<>();
        books.add(new Book("B1", "일반", "테스트책1", true, true, LocalDate.now(), userId));
        given(bookMapper.getRentedBooks(userId)).willReturn(books);

        // when
        List<Book> result = bookService.getRentedBooks(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("ID로 도서 조회 성공 테스트")
    void getBookById_Success() {
        // given
        String bookId = "B1";
        Book book = new Book(bookId, "일반", "테스트책1", true, false, null, null);
        given(bookMapper.findById(bookId)).willReturn(book);

        // when
        Book result = bookService.getBookById(bookId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookId);
    }

    @Test
    @DisplayName("ID로 도서 조회 실패 테스트 - 존재하지 않는 도서")
    void getBookById_Fail() {
        // given
        String bookId = "B999";
        given(bookMapper.findById(bookId)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> bookService.getBookById(bookId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 번호의 도서는 존재하지 않습니다.");
    }

    @Test
    @DisplayName("제목으로 도서 검색 테스트 - 부분 일치")
    void getBookByTitle_Partial() {
        // given
        String title = "테스트";
        List<Book> books = new ArrayList<>();
        books.add(new Book("B1", "일반", "테스트책1", true, false, null, null));
        given(bookMapper.findByTitle(title)).willReturn(books);

        // when
        List<Book> result = bookService.getBookByTitle(title, false);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("제목으로 도서 검색 테스트 - 완전 일치")
    void getBookByTitle_Exact() {
        // given
        String title = "테스트책1";
        List<Book> books = new ArrayList<>();
        books.add(new Book("B1", "일반", "테스트책1", true, false, null, null));
        given(bookMapper.findByTitleFit(title)).willReturn(books);

        // when
        List<Book> result = bookService.getBookByTitle(title, true);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("도서 대출 성공 테스트")
    void checkOutBook_Success() {
        // given
        String bookId = "B1";
        String userId = "U1";
        Book book = new Book(bookId, "일반", "테스트책1", true, false, null, null);
        User user = new User(userId, "password", "홍길동", "normal");

        given(bookMapper.findById(bookId)).willReturn(book);
        given(userMapper.findById(userId)).willReturn(user);
        given(bookMapper.countOverdueBooks(userId)).willReturn(0);

        // when
        Book result = bookService.checkOutBook(bookId, userId);

        // then
        assertThat(result.isRented()).isTrue();
        assertThat(result.getUserId()).isEqualTo(userId);
        verify(bookMapper).updateBook(any(Book.class));
    }

    @Test
    @DisplayName("도서 대출 실패 테스트 - 이미 대출중")
    void checkOutBook_Fail_AlreadyRented() {
        // given
        String bookId = "B1";
        String userId = "U1";
        Book book = new Book(bookId, "일반", "테스트책1", true, true, LocalDate.now(), "U2");

        given(bookMapper.findById(bookId)).willReturn(book);

        // when & then
        assertThatThrownBy(() -> bookService.checkOutBook(bookId, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 제목의 책은 이미 대출되어 있습니다.");
    }

    @Test
    @DisplayName("도서 반납 성공 테스트")
    void checkInBook_Success() {
        // given
        String bookId = "B1";
        String userId = "U1";
        Book book = new Book(bookId, "일반", "테스트책1", true, true, LocalDate.now(), userId);
        User user = new User(userId, "password", "홍길동", "normal");

        given(bookMapper.findById(bookId)).willReturn(book);
        given(userMapper.findById(userId)).willReturn(user);

        // when
        Book result = bookService.checkInBook(bookId, userId);

        // then
        assertThat(result.isRented()).isFalse();
        assertThat(result.getUserId()).isNull();
        verify(bookMapper).updateBook(any(Book.class));
    }
}
