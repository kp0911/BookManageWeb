package combookmanageweb.bookmanageweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

// 💡 프로젝트 내의 모든 @RestController에서 발생하는 예외를 감시하고 가로챕니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🚀 1. 우리가 비즈니스 로직(가드 클로즈)에서 던진 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

        // ErrorResponse 객체에 에러 내용을 예쁘게 담습니다.
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), // 400 Bad Request
                ex.getMessage(),                // 우리가 서비스에 작성한 안내 문구
                LocalDateTime.now()
        );

        // JSON 형태와 함께 400 상태 코드를 클라이언트에게 반환합니다.
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 🚀 2. 개발자가 예상치 못한 서버 내부의 런타임 에러 처리 (안전망)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {

        // 서버가 죽는 대신 500 에러를 정형화해서 내려줍니다.
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500 Internal Server Error
                "서버 내부에서 알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.",
                LocalDateTime.now()
        );

        // (실무에서는 여기에 log.error() 등을 써서 서버 로그에 진짜 에러 내역을 남깁니다)
        ex.printStackTrace(); // 콘솔에만 에러 기록

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}