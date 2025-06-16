package ninegle.Readio.mail.user.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import ninegle.Readio.mail.common.service.EmailService
import ninegle.Readio.user.domain.User

@Service
class UserMailSender(
    private val emailService: EmailService,
    private val templateProvider: UserMailTemplateProvider
) {
    private val log = LoggerFactory.getLogger(UserMailSender::class.java)

    @Async
    fun sendSignupMail(user: User) {
        val subject = "[Readio] 회원가입을 환영합니다!"
        val body = templateProvider.buildSignupMailBody(user.getNickname())
        emailService.send(user.email, subject, body) //user변환 후 확인
    }
}