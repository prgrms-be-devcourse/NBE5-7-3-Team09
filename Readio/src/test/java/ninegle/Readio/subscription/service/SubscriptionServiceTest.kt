package ninegle.Readio.subscription.service

import io.mockk.*
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto
import ninegle.Readio.subscription.mapper.SubscriptionMapper
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.service.UserContextService
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

class SubscriptionServiceTest {

    private lateinit var subscriptionRepository: SubscriptionRepository
    private lateinit var subscriptionMapper: SubscriptionMapper
    private lateinit var userContextService: UserContextService
    private lateinit var subscriptionManager: SubscriptionManager
    private lateinit var subscriptionService: SubscriptionService

    //mockk을 통해 모든 의존성을 가짜로 만들어줌
    @BeforeEach
    fun setUp() {
        subscriptionRepository = mockk()
        subscriptionMapper = mockk()
        userContextService = mockk()
        subscriptionManager = mockk()
        subscriptionService = SubscriptionService(
            subscriptionRepository,
            subscriptionMapper,
            userContextService,
            subscriptionManager
        )
    }

    //mockk초기화
    @AfterEach
    fun shutDown() {
        clearAllMocks()
    }

    @Test
    @DisplayName("getSubscription - 구독이 존재할 경우 DTO 반환")
    fun `구독이 존재할 경우 - DTO를 반환하는지 테스트`() {
        val userId = 1L
        val subscription = mockk<Subscription>()
        val dto = mockk<SubscriptionResponseDto>()

        //user id를 가져오고
        every { userContextService.getCurrentUserId() } returns userId
        //구독을 찾아오고
        every { subscriptionRepository.findByUserId(userId) } returns subscription
        //Dto로 변환
        every { subscriptionMapper.toDto(subscription) } returns dto

        val result = subscriptionService.getSubscription()

        assertEquals(dto, result)
        verify {
            userContextService.getCurrentUserId()
            subscriptionRepository.findByUserId(userId)
            subscriptionMapper.toDto(subscription)
        }
    }

    @Test
    @DisplayName("getSubscription - 구독이 존재하지 않으면 null 반환")
    fun `구독이 없을 경우 null 반환 테스트`() {
        val userId = 1L
        every { userContextService.getCurrentUserId() } returns userId
        every { subscriptionRepository.findByUserId(userId) } returns null

        val result = subscriptionService.getSubscription()

        assertNull(result)
        verify {
            userContextService.getCurrentUserId()
            subscriptionRepository.findByUserId(userId)
        }
        verify(exactly = 0) { subscriptionMapper.toDto(any()) }
    }

    @Test
    @DisplayName("createSubscription - subscriptionManager에 위임")
    fun `구독생성을 subscriptionManager 구독으로 보냈는지 테스트`() {
        val userId = 1L
        every { userContextService.getCurrentUserId() } returns userId
        every { subscriptionManager.subscribe(userId) } just Runs

        subscriptionService.createSubscription()

        verify {
            userContextService.getCurrentUserId()
            subscriptionManager.subscribe(userId)
        }
    }

    @Test
    @DisplayName("cancelSubscription - 올바른 ID와 구독 존재 시 manager 호출")
    fun `구독 존재할 때, subscriptionManager에서 취소가 호출되는지 테스트`() {
        val userId = 1L
        val subscriptionId = 1L
        val subscription = mockk<Subscription>()

        every { userContextService.getCurrentUserId() } returns userId
        every { subscriptionRepository.findByUserId(userId) } returns subscription
        every { subscriptionManager.cancelSubscription(userId) } just Runs

        subscriptionService.cancelSubscription(subscriptionId)

        verify {
            userContextService.getCurrentUserId()
            subscriptionRepository.findByUserId(userId)
            subscriptionManager.cancelSubscription(userId)
        }
    }

    @Test
    @DisplayName("cancelSubscription - ID가 1이 아니면 예외")
    fun `구독이 존재할 때, 구독id가 1인경우 예외가 발생하는지 테스트`() {
        val invalidIds = listOf(0L, 2L, 999L, -1L)

        invalidIds.forEach { invalidId ->
            val exception = assertThrows<BusinessException> {
                subscriptionService.cancelSubscription(invalidId)
            }
            assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, exception.errorCode)
        }
    }

    @Test
    @DisplayName("cancelSubscription - 구독이 존재하지 않으면 예외")
    fun `구독이 없는 경우 예외가 발생하는지 테스트`() {
        val userId = 1L
        every { userContextService.getCurrentUserId() } returns userId
        every { subscriptionRepository.findByUserId(userId) } returns null

        val exception = assertThrows<BusinessException> {
            subscriptionService.cancelSubscription(1L)
        }

        assertEquals(ErrorCode.SUBSCRIPTION_NOT_FOUND, exception.errorCode)
        verify {
            userContextService.getCurrentUserId()
            subscriptionRepository.findByUserId(userId)
        }
    }
}