package ninegle.Readio.mail.subscription.service

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ninegle.Readio.mail.common.service.EmailService
import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.user.domain.User
import java.time.LocalDate

class SubscriptionMailSenderTest {

    private val emailService = mockk<EmailService>()
    private val templateProvider = mockk<SubscriptionMailTemplateProvider>()

    private lateinit var subscriptionMailSender: SubscriptionMailSender
    private lateinit var testUser: User
    private lateinit var testSubscription: Subscription

    @BeforeEach
    fun setUp() {
        clearAllMocks()

        subscriptionMailSender = SubscriptionMailSender(emailService, templateProvider)

        testUser = User(
            email = "test@readio.com",
            password = "password123",
            nickname = "테스트유저",
            phoneNumber = "010-1234-5678"
        ).apply {
            id = 1L
        }

        testSubscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.of(2025, 6, 1),
            expDate = LocalDate.of(2025, 7, 1)
        )
    }

    @Test
    fun `구독 결제 완료 메일 발송 테스트 - 정상 케이스`() {
        val expectedSubject = "[Readio] 구독 결제가 완료되었습니다."
        val expectedBody = """
            안녕하세요, 테스트유저님.
            Readio의 세계에 오신 것을 진심으로 환영합니다!
            
            테스트유저님께서 구독을 완료해 주셨습니다.
            • 구독 시작일: 2025-06-01
            • 구독 종료일: 2025-07-01
        """.trimIndent()

        every { templateProvider.buildSubscribeMailBody(testUser.nickname, testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendSubscribeMail(testUser, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildSubscribeMailBody(testUser.nickname, testSubscription)
        }
        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `구독 취소 메일 발송 테스트 - 정상 케이스`() {
        val expectedSubject = "[Readio] 구독이 취소되었습니다."
        val expectedBody = """
            안녕하세요, 테스트유저님.
            테스트유저님의 구독이 성공적으로 취소되었습니다.
            • 종료일: 2025-07-01
        """.trimIndent()

        every { templateProvider.buildCancelMailBody(testUser.nickname, testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendCancelMail(testUser, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildCancelMailBody(testUser.nickname, testSubscription)
        }
        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `구독 만료 임박 메일 발송 테스트 - 정상 케이스`() {
        val expectedSubject = "[Readio] 구독이 곧 만료됩니다 (1일 전)"
        val expectedBody = """
            안녕하세요, 테스트유저님.
            회원님의 구독이 내일인 2025-07-01에 만료됩니다.
            • 구독 종료일: 2025-07-01
        """.trimIndent()

        every { templateProvider.buildExpirationSoonMailBody(testUser.nickname, testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendExpirationSoonMail(testUser, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildExpirationSoonMailBody(testUser.nickname, testSubscription)
        }
        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `구독 만료 당일 메일 발송 테스트 - 정상 케이스`() {
        val expectedSubject = "[Readio] 구독이 오늘 만료됩니다"
        val expectedBody = """
            안녕하세요, 테스트유저님.
            회원님의 구독이 오늘인 2025-07-01에 만료됩니다.
            • 구독 종료일: 2025-07-01
        """.trimIndent()

        every { templateProvider.buildExpirationTodayMailBody(testUser.nickname, testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendExpirationTodayMail(testUser, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildExpirationTodayMailBody(testUser.nickname, testSubscription)
        }
        verify(exactly = 1) {
            emailService.send(testUser.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `템플릿 생성 실패 시 EmailService 호출되지 않음`() {
        every { templateProvider.buildSubscribeMailBody(any(), any()) } throws RuntimeException("Template generation failed")
        every { emailService.send(any(), any(), any()) } just Runs

        assertThrows<RuntimeException> {
            subscriptionMailSender.sendSubscribeMail(testUser, testSubscription)
        }

        verify(exactly = 1) {
            templateProvider.buildSubscribeMailBody(testUser.nickname, testSubscription)
        }
        verify(exactly = 0) {
            emailService.send(any(), any(), any())
        }
    }

    @Test
    fun `빈 닉네임으로 메일 발송 테스트`() {
        val userWithEmptyNickname = User(
            email = "test@readio.com",
            password = "password123",
            nickname = "",
            phoneNumber = "010-1234-5678"
        ).apply { id = 1L }

        val expectedSubject = "[Readio] 구독 결제가 완료되었습니다."
        val expectedBody = "안녕하세요, 회원님."

        every { templateProvider.buildSubscribeMailBody("", testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendSubscribeMail(userWithEmptyNickname, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildSubscribeMailBody("", testSubscription)
        }
        verify(exactly = 1) {
            emailService.send(userWithEmptyNickname.email, expectedSubject, expectedBody)
        }
    }

    @Test
    fun `빈 이메일 주소로 메일 발송 테스트`() {
        val userWithEmptyEmail = User(
            email = "",
            password = "password123",
            nickname = "테스트유저",
            phoneNumber = "010-1234-5678"
        ).apply { id = 1L }

        val expectedSubject = "[Readio] 구독 결제가 완료되었습니다."
        val expectedBody = "테스트 메일 본문"

        every { templateProvider.buildSubscribeMailBody(userWithEmptyEmail.nickname, testSubscription) } returns expectedBody
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendSubscribeMail(userWithEmptyEmail, testSubscription)

        verify(exactly = 1) {
            templateProvider.buildSubscribeMailBody(userWithEmptyEmail.nickname, testSubscription)
        }
        verify(exactly = 1) {
            emailService.send("", expectedSubject, expectedBody)
        }
    }

    @Test
    fun `모든 메일 타입의 제목이 올바른지 확인 테스트`() {
        every { templateProvider.buildSubscribeMailBody(any(), any()) } returns "subscribe body"
        every { templateProvider.buildCancelMailBody(any(), any()) } returns "cancel body"
        every { templateProvider.buildExpirationSoonMailBody(any(), any()) } returns "expiration soon body"
        every { templateProvider.buildExpirationTodayMailBody(any(), any()) } returns "expiration today body"
        every { emailService.send(any(), any(), any()) } just Runs

        subscriptionMailSender.sendSubscribeMail(testUser, testSubscription)
        subscriptionMailSender.sendCancelMail(testUser, testSubscription)
        subscriptionMailSender.sendExpirationSoonMail(testUser, testSubscription)
        subscriptionMailSender.sendExpirationTodayMail(testUser, testSubscription)

        verifySequence {
            emailService.send(testUser.email, "[Readio] 구독 결제가 완료되었습니다.", "subscribe body")
            emailService.send(testUser.email, "[Readio] 구독이 취소되었습니다.", "cancel body")
            emailService.send(testUser.email, "[Readio] 구독이 곧 만료됩니다 (1일 전)", "expiration soon body")
            emailService.send(testUser.email, "[Readio] 구독이 오늘 만료됩니다", "expiration today body")
        }
    }
}