package ninegle.Readio.tosspay.service;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.tosspay.config.TossApiClient;
import ninegle.Readio.tosspay.dto.PaymentSuccessResponseDto;
import ninegle.Readio.tosspay.dto.TossPaymentConfirmRequestDto;
import ninegle.Readio.tosspay.dto.TossPaymentConfirmResponseDto;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.repository.UserRepository;
import ninegle.Readio.user.service.UserContextService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final TossApiClient tossApiClient;
	private final UserContextService userContextService;
	private final UserRepository userRepository;

	@Value("${toss.secret-key}")
	private String secretKey;

	@Transactional
	public PaymentSuccessResponseDto confirmPayment(
		TossPaymentConfirmRequestDto tossRequest) {

		//유저 검증
		Long userId = userContextService.getCurrentUserId();
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND); //404
		}

		//인증 헤더 생성
		String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes());

		// Feign 요청
		TossPaymentConfirmResponseDto response = tossApiClient.confirmPayment(
			"Basic " + encodedKey, tossRequest);

		if (!"DONE".equals(response.status())) {
			throw new BusinessException(ErrorCode.INVALID_REQUEST_DATA); //400
		}

		User user = userOptional.get();
		Long point = tossRequest.amount();

		if (point < 1) {
			throw new BusinessException(ErrorCode.ZERO_AMOUNT_PAYMENT_NOT_ALLOWED); // 0원 결제 시 400
		}
		user.setPoint(user.getPoint() + point);

		//객체 생성 후 반환 코드 필요
		PaymentSuccessResponseDto ResponseDto = PaymentSuccessResponseDto.builder()
			.orderId(tossRequest.orderId())
			.amount(tossRequest.amount()).build();

		return ResponseDto;
	}
}