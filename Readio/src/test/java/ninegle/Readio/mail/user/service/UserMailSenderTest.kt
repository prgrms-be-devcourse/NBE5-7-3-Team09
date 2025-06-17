package ninegle.Readio.mail.user.service

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ninegle.Readio.mail.common.service.EmailService
import ninegle.Readio.user.domain.User

class UserMailSenderTest {

    private val emailService = mockk<EmailService>()
    private val templateProvider = mockk<UserMailTemplateProvider>()

    private lateinit var userMailSender: UserMailSender
    private lateinit var testUser: User

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        userMailSender = UserMailSender(emailService, templateProvider)

        testUser = User(
            email = "test@readio.com",
            password = "password123",
            nickname = "테스트유저",
            phoneNumber = "010-1234-5678"
        ).apply {
            id = 1L
        }
    }

    @Test
    fun `회원가입 메일 발송 테스트 - 정상 케이스`() {
        val expectedSubject = "[Readio] 회원가입을 환영합니다!"
        val expectedBody = """
            안녕하세요, 테스트유저님.
            Readio 회원가입을 진심으로 환영합니다!
            
            다양한 전자책과 함께 즐거운 독서 경험을 만들어보세요.
        """.trimIndent()

        every { templateProvider.buildSignupMailBody(testUser.nickname) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        userMailSender.sendSignupMail(testUser)

        verify(exactly = 1) {
            templateProvider.buildSignupMailBody(testUser.nickname)
        }
        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `회원가입 메일 발송 테스트 - 빈 닉네임`() {
        val userWithEmptyNickname = User(
            email = "test@readio.com",
            password = "password123",
            nickname = "",
            phoneNumber = "010-1234-5678"
        ).apply { id = 1L }

        val expectedSubject = "[Readio] 회원가입을 환영합니다!"
        val expectedBody = "안녕하세요, 회원님."

        every { templateProvider.buildSignupMailBody("") } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        userMailSender.sendSignupMail(userWithEmptyNickname)

        verify(exactly = 1) {
            templateProvider.buildSignupMailBody("")
        }
        verify(exactly = 1) {
            emailService.send(userWithEmptyNickname.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `회원가입 메일 발송 테스트 - 빈 이메일 주소`() {
        val userWithEmptyEmail = User(
            email = "",
            password = "password123",
            nickname = "테스트유저",
            phoneNumber = "010-1234-5678"
        ).apply { id = 1L }

        val expectedSubject = "[Readio] 회원가입을 환영합니다!"
        val expectedBody = "테스트 메일 본문"

        every { templateProvider.buildSignupMailBody(userWithEmptyEmail.nickname) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        userMailSender.sendSignupMail(userWithEmptyEmail)

        verify(exactly = 1) {
            templateProvider.buildSignupMailBody(userWithEmptyEmail.nickname)
        }
        verify(exactly = 1) {
            emailService.send("", expectedSubject, expectedBody)
        }
    }

    @Test
    fun `템플릿 생성 실패 시 EmailService 호출되지 않음`() {
        every { templateProvider.buildSignupMailBody(any()) } throws RuntimeException("Template generation failed")
        every { emailService.send(any(), any(), any()) } just Runs

        assertThrows<RuntimeException> {
            userMailSender.sendSignupMail(testUser)
        }

        verify(exactly = 1) {
            templateProvider.buildSignupMailBody(testUser.nickname)
        }
        verify(exactly = 0) {
            emailService.send(any(), any(), any())
        }
    }

    @Test
    fun `특수문자 닉네임으로 메일 발송 테스트`() {
        val userWithSpecialNickname = User(
            email = "test@readio.com",
            password = "password123",
            nickname = "테스터@#$%",
            phoneNumber = "010-1234-5678"
        ).apply { id = 1L }

        val expectedSubject = "[Readio] 회원가입을 환영합니다!"
        val expectedBody = "특수문자 닉네임 메일 본문"

        every { templateProvider.buildSignupMailBody(userWithSpecialNickname.nickname) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        userMailSender.sendSignupMail(userWithSpecialNickname)

        verify(exactly = 1) {
            templateProvider.buildSignupMailBody(userWithSpecialNickname.nickname)
        }
        verify(exactly = 1) {
            emailService.send(userWithSpecialNickname.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `메일 제목이 정확한지 확인 테스트`() {
        val expectedSubject = "[Readio] 회원가입을 환영합니다!"
        val expectedBody = "테스트 메일 본문"

        every { templateProvider.buildSignupMailBody(any()) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        userMailSender.sendSignupMail(testUser)

        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }
}