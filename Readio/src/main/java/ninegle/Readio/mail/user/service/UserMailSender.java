package ninegle.Readio.mail.user.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.mail.common.service.EmailService;
import ninegle.Readio.user.domain.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMailSender {

	private final EmailService emailService;
	private final UserMailTemplateProvider templateProvider;

	@Async
	public void sendSignupMail(User user) {
		String subject = "[Readio] 회원가입을 환영합니다!";
		String body = templateProvider.buildSignupMailBody(user.getNickname());
		emailService.send(user.getEmail(), subject, body);
	}
}