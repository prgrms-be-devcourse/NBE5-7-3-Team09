package ninegle.Readio.mail.subscription.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.mail.common.service.EmailService;
import ninegle.Readio.subscription.domain.Subscription;
import ninegle.Readio.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionMailSender {

	private final EmailService emailService;
	private final SubscriptionMailTemplateProvider templateProvider;

	@Async
	public void sendSubscribeMail(User user, Subscription subscription) { // 구독 결제 완료
		String subject = "[Readio] 구독 결제가 완료되었습니다.";
		String body = templateProvider.buildSubscribeMailBody(user.getNickname(), subscription);

		emailService.send(user.getEmail(), subject, body);
	}

	@Async
	public void sendCancelMail(User user, Subscription subscription) { // 구독 취소
		String subject = "[Readio] 구독이 취소되었습니다.";
		String body = templateProvider.buildCancelMailBody(user.getNickname(), subscription);

		emailService.send(user.getEmail(), subject, body);
	}

	@Async
	public void sendExpirationSoonMail(User user, Subscription subscription) {
		String subject = "[Readio] 구독이 곧 만료됩니다 (1일 전)";
		String body = templateProvider.buildExpirationSoonMailBody(user.getNickname(), subscription);
		emailService.send(user.getEmail(), subject, body);
	}

	@Async
	public void sendExpirationTodayMail(User user, Subscription subscription) {
		String subject = "[Readio] 구독이 오늘 만료됩니다";
		String body = templateProvider.buildExpirationTodayMailBody(user.getNickname(), subscription);
		emailService.send(user.getEmail(), subject, body);
	}
}