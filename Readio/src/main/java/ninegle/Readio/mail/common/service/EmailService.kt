package ninegle.Readio.mail.common.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    private val log = LoggerFactory.getLogger(EmailService::class.java)

    fun send(to: String, subject: String, body: String) {
        try {
            val message = SimpleMailMessage().apply {
                setTo(to)
                setSubject(subject)
                setText(body)
            }
            mailSender.send(message)
            log.info("회원가입 메일 전송 완료: {}", to)
        } catch (e: Exception) {
            log.error("회원가입 메일 전송 실패: {}", to, e)
        }
    }
}