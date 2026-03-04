package combookmanageweb.bookmanageweb.mapper;

import combookmanageweb.bookmanageweb.dto.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface BookMapper {

    // 1. 전체 도서 조회 (기존 printAllBooks 역할)
    @Select("SELECT * FROM BOOK")
    List<Book> findAll();

    @Select("SELECT id, title, category, rentable, rented FROM BOOK")
    List<Book> getAvailableBooks();


    // 2. ID로 단건 도서 조회 (기존 getBookOrThrow의 검색 역할)
    @Select("SELECT * FROM BOOK WHERE id = #{id}")
    Book findById(String id);

    // 3. 도서 상태 업데이트 (대출/반납 시 상태 덮어쓰기 역할)
    @Update("UPDATE BOOK SET rented = #{rented}, returnDate = #{returnDate}, userId = #{userId} WHERE id = #{id}")
    void updateBook(Book book);

    // 4. 초기 데이터(더미 데이터)를 DB에 밀어넣을 때 사용할 Insert
    @Insert("INSERT INTO BOOK (id, category, title, rentable, rented, returnDate, userId) " +
            "VALUES (#{id}, #{category}, #{title}, #{rentable}, #{rented}, #{returnDate}, #{userId})")
    void insertBook(Book book);

    //5. 제목으로 책 찾기
    @Select("SELECT  * FROM  BOOK WHERE title LIKE CONCAT('%', #{title}, '%')")
    List<Book> findByTitle(String title);

    //연체된 책 가져오기 
    @Select("SELECT COUNT(*) FROM  BOOK WHERE userId = #{userId} AND returnDate < CURRENT_DATE")
    int countOverdueBooks(String userId);
}
