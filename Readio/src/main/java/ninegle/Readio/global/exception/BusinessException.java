package ninegle.Readio.global.exception;

import lombok.Getter;
import ninegle.Readio.global.exception.domain.ErrorCode;

@Getter
public class BusinessException extends RuntimeException {

	private final ErrorCode errorCode;

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
