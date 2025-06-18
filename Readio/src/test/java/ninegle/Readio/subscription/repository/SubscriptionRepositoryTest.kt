package ninegle.Readio.subscription.repository

import ninegle.Readio.subscription.domain.Subscription
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate

@DataJpaTest //db에 연결
class
SubscriptionRepositoryTest {

    @Autowired
    private lateinit var subscriptionRepository: SubscriptionRepository

    @Autowired //jpa직접 조작을 위해
    private lateinit var testEntityManager: TestEntityManager

    private lateinit var testSubscription: Subscription

    //테스트마다 사용할 구독 인스턴스를 초기화
    @BeforeEach
    fun setUp() {
        testSubscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )
    }

    @Test
    @DisplayName("구독을 저장했을 때, id가 생성되고 필드값이 올바른지 검증")
    fun `구독 저장 테스트`() {
        val subscription = testSubscription

        val savedSubscription = subscriptionRepository.save(subscription)

        assertNotNull(savedSubscription.id)
        assertEquals(subscription.userId, savedSubscription.userId)
        assertEquals(subscription.subDate, savedSubscription.subDate)
        assertEquals(subscription.expDate, savedSubscription.expDate)
        assertFalse(savedSubscription.canceled)
    }

    @Test
    @DisplayName("사용자 ID로 구독 조회 테스트 - 존재하는 경우")
    fun `사용자 ID로 구독 조회 테스트 - 존재하는 경우`() {
        val userId = 1L
        val savedSubscription = subscriptionRepository.save(testSubscription)
        testEntityManager.flush()
        testEntityManager.clear()

        val foundSubscription = subscriptionRepository.findByUserId(userId)

        assertNotNull(foundSubscription)
        assertEquals(savedSubscription.id, foundSubscription?.id)
        assertEquals(userId, foundSubscription?.userId)
    }

    @Test
    @DisplayName("사용자 ID로 구독 조회 테스트 - 존재하지 않는 경우")
    fun `사용자 ID로 구독 조회 테스트 - 존재하지 않는 경우`() {
        val nonExistentUserId = 999L

        val foundSubscription = subscriptionRepository.findByUserId(nonExistentUserId)

        assertNull(foundSubscription)
    }

    //findAllByUserId
    //특정 userId에 대해 단일 구독만 존재할 때, findAllByUserId 결과가 하나인지 확인
    @Test
    fun `사용자 ID로 모든 구독 조회 테스트 - 단일 구독`() {
        val userId = 1L
        subscriptionRepository.save(testSubscription)
        testEntityManager.flush()

        val subscriptions = subscriptionRepository.findAllByUserId(userId)

        assertEquals(1, subscriptions.size)
        assertEquals(userId, subscriptions[0].userId)
    }

    //존재하지 않는 userId로 구독 이력을 조회하면 빈 리스트가 반환되는지 확인
    @Test
    fun `사용자 ID로 모든 구독 조회 테스트 - 빈 리스트`() {
        val nonExistentUserId = 999L

        val subscriptions = subscriptionRepository.findAllByUserId(nonExistentUserId)

        assertTrue(subscriptions.isEmpty())
    }


    @Test
    @DisplayName("userId 유니크 제약조건 테스트")
    fun `userId 유니크 제약조건 테스트`() {
        val userId = 1L
        val subscription1 = Subscription.create(
            userId = userId,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )
        val subscription2 = Subscription.create(
            userId = userId,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )

        subscriptionRepository.save(subscription1)
        testEntityManager.flush()

        // 같은 userId로 두 번째 구독을 저장하려고 하면 예외가 발생해야 함
        assertThrows(Exception::class.java) {
            subscriptionRepository.save(subscription2)
            testEntityManager.flush()
        }
    }
}