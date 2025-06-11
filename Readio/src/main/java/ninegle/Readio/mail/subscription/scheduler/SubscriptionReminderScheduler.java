package ninegle.Readio.mail.subscription.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.mail.subscription.service.SubscriptionMailSender;
import ninegle.Readio.subscription.domain.Subscription;
import ninegle.Readio.subscription.repository.SubscriptionRepository;
import ninegle.Readio.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionReminderScheduler {

	private final SubscriptionRepository subscriptionRepository;
	private final UserRepository userRepository;
	private final SubscriptionMailSender mailSender;

	/**
	 * 매일 오전 10시에 실행
	 * 구독 종료 1일 전 또는 당일인 사용자에게 메일 발송
	 */
	//@Scheduled(cron = "0 * * * * *") // 발표 시연용: 매 분 실행
	@Scheduled(cron = "0 0 10 * * *") // 매일 오전 10시
	public void sendSubscriptionReminders() {
		LocalDate today = LocalDate.now();

		List<Subscription> subscriptions = subscriptionRepository.findAll().stream()
			.filter(sub ->
				sub.getExpDate().isEqual(today) || sub.getExpDate().isEqual(today.plusDays(1))
			)
			.toList();

		for (Subscription sub : subscriptions) {
			userRepository.findById(sub.getUserId()).ifPresent(user -> {
				if (sub.getExpDate().isEqual(today)) {
					mailSender.sendExpirationTodayMail(user, sub); // 종료 당일
				} else {
					mailSender.sendExpirationSoonMail(user, sub); // 하루 전
				}
			});
		}

		log.info("구독 만료 알림 메일 작업 완료: 총 {}건", subscriptions.size());
	}
}