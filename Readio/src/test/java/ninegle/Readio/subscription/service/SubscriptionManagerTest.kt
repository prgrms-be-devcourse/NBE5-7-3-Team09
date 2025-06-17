package ninegle.Readio.subscription.service

import io.mockk.*
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mail.subscription.service.SubscriptionMailSender
import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.repository.UserRepository
import org.junit.jupiter.api.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SubscriptionManagerTest {

    private lateinit var subscriptionRepository: SubscriptionRepository
    private lateinit var userRepository: UserRepository
    private lateinit var mailSender: SubscriptionMailSender
    private lateinit var subscriptionManager: SubscriptionManager

    private val userId = 1L
    private lateinit var user: User

    //mockk을 통해 모든 의존성을 가짜로 만들어줌
    @BeforeEach
    fun setUp() {
        subscriptionRepository = mockk()
        userRepository = mockk()
        mailSender = mockk()
        subscriptionManager = SubscriptionManager(subscriptionRepository, userRepository, mailSender)

        user = mockk(relaxed = true)
        every { user.id } returns userId
    }

    //mockk초기화
    @AfterEach
    fun shutDown() {
        clearAllMocks()
    }

    @Test
    fun `구독 정보가 없는 신규 사용자가 포인트를 가지고 있을 때 정상적으로 구독이 생성되는지 테스트`() {
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { subscriptionRepository.findByUserId(userId) } returns null
        every { user.point } returns 20000L
        every { subscriptionRepository.save(any()) } returns mockk()
        every { mailSender.sendSubscribeMail(any(), any()) } just Runs

        subscriptionManager.subscribe(userId)

        verify {
            userRepository.findById(userId)
            subscriptionRepository.findByUserId(userId)
            user.point = 5100L
            subscriptionRepository.save(any())
            mailSender.sendSubscribeMail(user, any())
        }
    }

    //기존에 취소된 구독이 있고, 기간도 만료된 경우 재구독이 가능함
    @Test
    fun `기존 구독자가 재구독하면 구독 정보 갱신`() {
        val subscription = mockk<Subscription>(relaxed = true)
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { subscriptionRepository.findByUserId(userId) } returns subscription
        every { subscription.isActive() } returns false
        every { subscription.canceled } returns true
        every { subscriptionRepository.save(any()) } returns subscription
        every { mailSender.sendSubscribeMail(user, subscription) } just Runs
        every { user.point } returns 20000L

        subscriptionManager.subscribe(userId)

        verify {
            user.point = 5100L
            subscription.updatePeriod(any(), any())
            subscription.uncancel()
            subscriptionRepository.save(subscription)
            mailSender.sendSubscribeMail(user, subscription)
        }
    }

    //아직 유효하고 취소되지 않은 구독 상태일 경우 구독 중복을 방지
    @Test
    fun `이미 활성 구독이면 예외 발생`() {
        val subscription = mockk<Subscription> {
            every { isActive() } returns true
            every { canceled } returns false
        }

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { subscriptionRepository.findByUserId(userId) } returns subscription

        val ex = assertFailsWith<BusinessException> {
            subscriptionManager.subscribe(userId)
        }

        assertEquals(ErrorCode.ALREADY_SUBSCRIBED, ex.errorCode)
    }

    //유효 기간이 남아있는 상태에서 취소만 된 구독 → 재구독 불가
    @Test
    fun `구독했지만 아직 만료 기간이 남았다면 재구독시 예외 발생`() {
        val subscription = mockk<Subscription> {
            every { isActive() } returns true
            every { canceled } returns true
        }

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { subscriptionRepository.findByUserId(userId) } returns subscription

        val ex = assertFailsWith<BusinessException> {
            subscriptionManager.subscribe(userId)
        }

        assertEquals(ErrorCode.ALREADY_SUBSCRIBED, ex.errorCode)
    }

    @Test
    fun `포인트 부족 시 구독 실패`() {
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { user.point } returns 1000L
        every { subscriptionRepository.findByUserId(userId) } returns null

        val ex = assertFailsWith<BusinessException> {
            subscriptionManager.subscribe(userId)
        }

        assertEquals(ErrorCode.NOT_ENOUGH_POINTS, ex.errorCode)
    }

    @Test
    fun `구독 취소 성공`() {
        val userId = 1L
        val user = mockk<User>(relaxed = true)

        val subscription = mockk<Subscription>(relaxed = true) {
            every { canceled } returns false
        }

        every { subscriptionRepository.findByUserId(userId) } returns subscription
        every { subscriptionRepository.save(subscription) } returns subscription
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { mailSender.sendCancelMail(user, subscription) } just Runs

        subscriptionManager.cancelSubscription(userId)

        verify(exactly = 1) {
            subscription.cancel()
            subscriptionRepository.save(subscription)
            mailSender.sendCancelMail(user, subscription)
        }
    }

    @Test
    fun `이미 취소된 구독은 또 취소할 수 없음`() {
        val subscription = mockk<Subscription> {
            every { canceled } returns true
        }

        every { subscriptionRepository.findByUserId(userId) } returns subscription

        val ex = assertFailsWith<BusinessException> {
            subscriptionManager.cancelSubscription(userId)
        }

        assertEquals(ErrorCode.SUBSCRIPTION_CANCELED, ex.errorCode)
    }

    @Test
    fun `구독이 존재하지 않으면 취소 실패`() {
        every { subscriptionRepository.findByUserId(userId) } returns null

        val ex = assertFailsWith<BusinessException> {
            subscriptionManager.cancelSubscription(userId)
        }

        assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, ex.errorCode)
    }
}