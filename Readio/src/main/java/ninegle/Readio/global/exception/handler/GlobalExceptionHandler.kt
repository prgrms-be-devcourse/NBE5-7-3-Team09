package ninegle.Readio.global.exception.handler

import jakarta.servlet.http.HttpServletRequest
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 비즈니스 로직에서 발생한 예외 처리 (ErrorCode 기반)
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        e: BusinessException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(e.errorCode.status).body(
            ErrorResponse(
                status = e.errorCode.status.value(),
                code = e.errorCode.name,
                message = e.errorCode.message,
                path = "${request.method} ${request.requestURI}"
            )
        )
    }

    /**
     * validation 적용된 필드 검증 실패시 예외 처리
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                code = HttpStatus.BAD_REQUEST.name,
                message = e.message,
                path = "${request.method} ${request.requestURI}"
            )
        )
    }


    // /**
    //  * 정의되지 않은 예외 처리 (서버 내부 오류 등)
    //  */
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ErrorResponse> handleUnknownException(Exception e) {
    // 	ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
    //
    // 	return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder()
    // 		.status(code.getStatus().value())
    // 		.code(code.name())
    // 		.message(code.getMessage())
    // 		.build());
    // }
}
