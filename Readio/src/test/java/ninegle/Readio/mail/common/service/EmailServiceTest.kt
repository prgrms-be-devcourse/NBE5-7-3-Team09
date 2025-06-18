package ninegle.Readio.mail.common.service

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender

class EmailServiceTest {

    private val mailSender = mockk<JavaMailSender>()
    private lateinit var emailService: EmailService

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        emailService = EmailService(mailSender)
    }

    @Test
    fun `메일 전송 테스트 - 정상 케이스`() {
        val to = "test@example.com"
        val subject = "테스트 제목"
        val body = "이것은 메일 테스트 입니다. 이것은 메일 테스트 입니다."

        every { mailSender.send(any<SimpleMailMessage>()) } just runs

        emailService.send(to, subject, body)

        verify(exactly = 1) {
            mailSender.send(match<SimpleMailMessage> { message ->
                message.to?.contains(to) == true &&
                        message.subject == subject &&
                        message.text == body
            })
        }
    }

    @Test
    fun `메일 전송 실패 시 예외 처리 테스트`() {
        val to = "test@example.com"
        val subject = "테스트 제목"
        val body = "이것은 메일 테스트 입니다. 이것은 메일 테스트 입니다."

        every { mailSender.send(any<SimpleMailMessage>()) } throws RuntimeException("메일 전송 실패")

        emailService.send(to, subject, body)

        verify(exactly = 1) {
            mailSender.send(any<SimpleMailMessage>())
        }
    }

    @Test
    fun `빈 이메일 주소로 메일 전송 테스트`() {
        val to = ""
        val subject = "테스트 제목"
        val body = "테스트 본문"

        every { mailSender.send(any<SimpleMailMessage>()) } just runs

        emailService.send(to, subject, body)

        verify(exactly = 1) {
            mailSender.send(match<SimpleMailMessage> { message ->
                message.to?.contains(to) == true &&
                        message.subject == subject &&
                        message.text == body
            })
        }
    }


    @Test
    fun `특수문자가 포함된 메일 내용으로 전송 테스트`() {
        val to = "test@example.com"
        val specialSubject = "[Readio] 특수문자 테스트 @#$%^&*()"
        val specialBody = """
            안녕하세요! @#$%^&*()
            특수문자 테스트입니다.
            
            이모지: ✨🎁💡
            한글: 안녕하세요
        """.trimIndent()

        every { mailSender.send(any<SimpleMailMessage>()) } just runs

        emailService.send(to, specialSubject, specialBody)

        verify(exactly = 1) {
            mailSender.send(match<SimpleMailMessage> { message ->
                message.to?.contains(to) == true &&
                        message.subject == specialSubject &&
                        message.text == specialBody
            })
        }
    }
}