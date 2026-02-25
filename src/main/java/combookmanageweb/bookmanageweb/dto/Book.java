package combookmanageweb.bookmanageweb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data // Getter, Setter, toString 등을 모두 자동 생성
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 자동 생성
public class Book {
    private String id;
    private String category;
    private String title;
    private boolean rentable; // 대출 가능 여부 (참고자료 구분용)
    private boolean rented;   // 현재 대출 중인지 여부
    private LocalDate returnDate; // 반납 예정일
    private String userId;

}