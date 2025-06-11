package ninegle.Readio.global.exception.domain;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	/*
	 * Commons : 공통 예외 처리
	 */
	// 400
	INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "요청 데이터가 올바르지 않습니다. 입력 데이터를 확인해 주세요."),
	INVALID_PAGINATION_PARAMETER(HttpStatus.BAD_REQUEST,
		"요청 파라미터가 유효하지 않습니다. page는 1 이상, size는 1 이상 50 이하로 설정 해야 합니다."),

	// 401
	AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요한 요청입니다. 로그인 해주세요."),

	// 404
	BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 책을 찾을 수 없습니다."),
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),

	// 500
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예기치 못한 오류가 발생했습니다."),

	/*
	 * Books : 책 예외 처리
	 */
	// 400
	INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "올바르지 않는 파일 형식 입니다."),

	// 409
	BOOK_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 책입니다."),
	DUPLICATE_ISBN(HttpStatus.CONFLICT, "이미 등록된 ISBN입니다."),
	DUPLICATE_ECN(HttpStatus.CONFLICT, "이미 등록된 ECN입니다."),
	DUPLICATE_NAME(HttpStatus.CONFLICT, "이미 존재하는 이름입니다."),

	/*
	 * Library : 라이브러리 예외 처리
	 */
	// 400
	NEGATIVE_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "페이지 수는 음수일 수 없습니다."),

	// 404
	BOOK_READ_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 책 열람 기록을 찾을 수 없습니다."),
	LIBRARY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 라이브러리를 찾을 수 없습니다."),

	// 409
	LIBRAY_NAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 라이브러리명입니다."),
	BOOK_ALREADY_IN_READING_LIST(HttpStatus.CONFLICT, "이미 읽고 있는 책 목록에 존재합니다."),

	/*
	 * Preference : 관심 도서 예외 처리
	 */

	//409
	BOOK_ALREADY_IN_PREFERENCE(HttpStatus.CONFLICT, "이미 관심 도서로 등록된 책입니다."),
	//404
	PREFERENCE_NOT_FOUND(HttpStatus.NOT_FOUND, "관심 도서로 등록되지 않은 책입니다."),

	/*
	 * Subscription : 구독 예외 처리
	 */
	// 400
	INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "이메일 형식이 올바르지 않습니다."),
	MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "필수 입력 항목을 입력해 주세요."),

	// 404
	SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 구독입니다."),
	USER_SUBSCRIPTIONS_EMPTY(HttpStatus.NOT_FOUND, "사용자의 구독 내역이 존재하지 않습니다."),

	/*
	 * Viewer : 뷰어 예외 처리
	 */
	// 403
	FORBIDDEN_BOOK_ACCESS(HttpStatus.FORBIDDEN, "이 책에 대한 열람 권한이 없습니다."),

	// 404
	PUBLISHER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 출판사를 찾을 수 없습니다."),

	// 409
	PUBLISHER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 출판사입니다."),

	/*
	 * review 관련 예외 처리
	 */
	//400
	RATING_OUT_OF_BOUNDARY(HttpStatus.BAD_REQUEST, "평점의 범위는 1.0부터 5.0입니다."),
	/*
	 * mypage : 관련 예외 처리
	 */
	//400
	ALREADY_SUBSCRIBED(HttpStatus.BAD_REQUEST, "이미 구독중인 구독권이 있습니다."),
	//400
	NOT_ENOUGH_POINTS(HttpStatus.BAD_REQUEST, "보유 포인트가 부족합니다."),
	//404
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	//400
	SUBSCRIPTION_CANCELED(HttpStatus.BAD_REQUEST, "이미 취소된 구독입니다."),

	/*
	 * Auth : 로그인 / 인증 관련 예외 처리
	 */

	// 401 Unauthorized
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
	ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
	REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
	INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
	TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "서버에 저장된 토큰과 일치하지 않습니다."),
	BLACKLISTED_TOKEN(HttpStatus.UNAUTHORIZED, "로그아웃된 토큰입니다. 다시 로그인 해주세요."),
	AUTH_HEADER_MISSING(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 누락되었습니다."),

	// 403 Forbidden
	FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 리소스에 접근할 권한이 없습니다."),

	// 404 Not Found
	LOGIN_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "로그인 정보와 일치하는 사용자가 존재하지 않습니다."),

	/*
	 * 리뷰관련 예외처리
	 */
	//404
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을수 없습니다."),

	// 토스페이
	// 400
	ZERO_AMOUNT_PAYMENT_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "0원 결제는 허용되지 않습니다."),

	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

}