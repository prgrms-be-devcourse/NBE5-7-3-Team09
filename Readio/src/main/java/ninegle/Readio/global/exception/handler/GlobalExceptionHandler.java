package ninegle.Readio.global.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.global.exception.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 비즈니스 로직에서 발생한 예외 처리 (ErrorCode 기반)
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
		ErrorCode code = e.getErrorCode();

		String path = request.getMethod() + " " + request.getRequestURI();

		return ResponseEntity.status(code.getStatus()).body(ErrorResponse.builder()
			.status(code.getStatus().value())
			.code(code.name())
			.message(code.getMessage())
			.path(path)
			.build());
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
