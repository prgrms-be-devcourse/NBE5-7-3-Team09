package ninegle.Readio.subscription.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto;
import ninegle.Readio.subscription.service.SubscriptionService;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

	private final SubscriptionService subscriptionService;

	@GetMapping
	public ResponseEntity<BaseResponse<SubscriptionResponseDto>> getSubscription() {
		SubscriptionResponseDto response = subscriptionService.getSubscription();

		// 구독 정보가 있는 경우 메시지와 함께 반환
		if (response != null) {
			return BaseResponse.ok("조회에 성공하였습니다.", response, HttpStatus.OK);
		} else {
			// 구독 정보가 없는 경우 메시지만 반환
			return BaseResponse.ok("존재하는 구독이 없습니다.", null, HttpStatus.OK);
		}
	}

	@PostMapping
	public ResponseEntity<BaseResponse<Void>> createSubscription() {
		subscriptionService.createSubscription();

		return BaseResponse.okOnlyStatus(HttpStatus.OK);
	}

	@DeleteMapping("/{subscription_id}")
	public ResponseEntity<BaseResponse<Void>> cancelSubscription(
		@PathVariable("subscription_id") Long subscriptionId) {
		subscriptionService.cancelSubscription(subscriptionId);

		return BaseResponse.okOnlyStatus(HttpStatus.OK);
	}
}