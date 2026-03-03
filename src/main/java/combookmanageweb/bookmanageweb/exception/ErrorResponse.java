package combookmanageweb.bookmanageweb.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;            // HTTP 상태 코드 (예: 400, 500)
    private String message;        // 에러 상세 메시지
    private LocalDateTime time;    // 에러 발생 시간
}