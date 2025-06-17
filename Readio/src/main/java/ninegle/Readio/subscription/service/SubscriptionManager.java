package ninegle.Readio.subscription.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.mail.subscription.service.SubscriptionMailSender;
import ninegle.Readio.subscription.domain.Subscription;
import ninegle.Readio.subscription.repository.SubscriptionRepository;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.repository.UserRepository;

/**
 * 구독 관련 핵심 비즈니스 로직을 처리하는 서비스
 * 구독 생성, 갱신, 취소 등의 핵심 기능을 담당
 */
@Service
@RequiredArgsConstructor
public class SubscriptionManager {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	private final SubscriptionMailSender mailSender;

	// 구독 비용
	private static final long SUBSCRIPTION_COST = 14900;

	/**
	 * 구독 생성 또는 갱신 처리
	 * - 새 사용자면 새 구독 생성
	 * - 기존 사용자면 구독 상태에 따라 적절히 처리
	 */
	@Transactional
	public void subscribe(Long userId) {
		// 사용자 존재 여부 확인 (없으면 예외 발생)
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 구독 정보 조회
		Optional<Subscription> optional = subscriptionRepository.findByUserId(userId);

		// 오늘 날짜와 만료일 계산 (1개월 구독)
		LocalDate now = LocalDate.now();
		LocalDate exp = now.plusMonths(1);

		// 기존 구독이 있는 경우
		if (optional.isPresent()) {
			Subscription subscription = optional.get();

			// 아직 유효한 구독이 있고 취소하지 않은 상태라면 - 중복 구독 불가!
			if (subscription.isActive() && !subscription.isCanceled()) {
				throw new BusinessException(ErrorCode.ALREADY_SUBSCRIBED);
			}

			// 취소된 구독인 경우
			if (subscription.isCanceled()) {
				// 취소했지만 아직 만료일이 안 지났으면 - 재구독 불가!
				// 즉, 이전에 취소했어도 구독 기간이 남아있으면 새로 결제 못함
				if (subscription.isActive()) {
					throw new BusinessException(ErrorCode.ALREADY_SUBSCRIBED);
				}
				// 취소했고 만료일도 지났으면 - 재구독 가능!
			}

			// 포인트 차감 (부족하면 예외 발생)
			chargePoints(user);

			// 구독 기간 업데이트
			subscription.updatePeriod(now, exp);

			// 취소 상태였다면 취소 표시 해제
			if (subscription.isCanceled()) {
				subscription.uncancel();
			}

			// 변경된 구독 정보 저장
			subscriptionRepository.save(subscription);

			// 구독 갱신 메일 전송
			mailSender.sendSubscribeMail(user, subscription);
			return;
		}

		// 신규 구독자인 경우: 새 구독 생성
		chargePoints(user);
		Subscription newSubscription = Subscription.builder()
			.userId(userId)
			.subDate(now)
			.expDate(exp)
			.canceled(false)
			.build();

		// 새 구독 정보 저장
		subscriptionRepository.save(newSubscription);

		// 신규 구독 안내 메일 전송
		mailSender.sendSubscribeMail(user, newSubscription);
	}

	/**
	 * 구독 취소 처리
	 * - 구독 자체는 삭제하지 않고 취소 표시만 함
	 * - 취소해도 만료일까지는 서비스를 이용가능하도록 하기 위해서
	 */
	@Transactional
	public void cancelSubscription(Long userId) {
		// 사용자의 구독 정보 조회 (없으면 예외 발생)
		Subscription subscription = subscriptionRepository.findByUserId(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

		// 이미 취소된 구독이면 또 취소할 수 없음
		if (subscription.isCanceled()) {
			throw new BusinessException(ErrorCode.SUBSCRIPTION_CANCELED);
		}

		// 구독 취소 처리
		subscription.cancel();
		subscriptionRepository.save(subscription);

		// 사용자 정보 조회 (해당 사용자에게 메일 발송용)
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		// 구독 취소 안내 메일 전송
		mailSender.sendCancelMail(user, subscription);
	}

	/**
	 * 구독 결제를 위한 포인트 차감
	 * - 포인트가 부족하면 결제 불가
	 */
	private void chargePoints(User user) {
		// 포인트가 구독 비용보다 적으면 결제 불가
		if (user.getPoint() < SUBSCRIPTION_COST) {
			throw new BusinessException(ErrorCode.NOT_ENOUGH_POINTS);
		}

		// 포인트 차감
		user.setPoint(user.getPoint() - SUBSCRIPTION_COST);
	}
}