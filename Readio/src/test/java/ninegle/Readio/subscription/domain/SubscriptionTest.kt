package ninegle.Readio.subscription.domain

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import java.time.LocalDate

class SubscriptionTest {

    @Test
    @DisplayName("기본 생성자로 구독 생성 테스트")
    fun `기본 생성자로 구독 생성 테스트`() {
        // JPA가 사용하는 기본 생성자
        val subscription = Subscription()

        assertNotNull(subscription)
        assertEquals(0L, subscription.userId)
        assertFalse(subscription.canceled)
        assertEquals(LocalDate.now(), subscription.subDate)
        assertEquals(LocalDate.now(), subscription.expDate)
    }

    @Test
    @DisplayName("매개변수 생성자로 구독 생성 테스트")
    fun `매개변수 생성자로 구독 생성 테스트`() {

        val userId = 1L
        val subDate = LocalDate.now()
        val expDate = subDate.plusMonths(1)

        // 내가 직접 값을 넣는 생성자
        val subscription = Subscription(
            userId = userId,
            subDate = subDate,
            expDate = expDate,
            canceled = false
        )

        assertEquals(userId, subscription.userId)
        assertEquals(subDate, subscription.subDate)
        assertEquals(expDate, subscription.expDate)
        assertFalse(subscription.canceled)
        assertTrue(subscription.isActive())
    }

    @Test
    @DisplayName("companion object create 메서드로 구독 생성 테스트")
    fun `companion object create 메서드로 구독 생성 테스트`() {

        val userId = 1L
        val subDate = LocalDate.now()
        val expDate = subDate.plusMonths(1)

        //팩토리 메서드
        val subscription = Subscription.create(userId, subDate, expDate)

        assertEquals(userId, subscription.userId)
        assertEquals(subDate, subscription.subDate)
        assertEquals(expDate, subscription.expDate)
        assertFalse(subscription.canceled)
        assertTrue(subscription.isActive())
    }

    @Test
    @DisplayName("canceled = true")
    fun `구독 취소 테스트`() {

        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )

        subscription.cancel()

        assertTrue(subscription.canceled)
    }

    @Test
    @DisplayName("canceled = false")
    fun `구독 취소 해제 테스트`() {
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )
        subscription.cancel()

        subscription.uncancel()

        assertFalse(subscription.canceled)
    }

    @Test
    @DisplayName("구독 유효성 확인 - 활성 구독 (만료일이 오늘)")
    fun `구독 유효성 확인 - 활성 구독 오늘 만료`() {
        // 오늘이 만료일인 구독
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now().minusMonths(1),
            expDate = LocalDate.now()
        )

        assertTrue(subscription.isActive())
    }

    @Test
    @DisplayName("구독 유효성 확인 - 활성 구독 (만료일이 미래)")
    fun `구독 유효성 확인 - 활성 구독 미래 만료`() {
        // 만료일이 미래인(남은) 구독
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusDays(10)
        )

        assertTrue(subscription.isActive())
    }

    @Test
    @DisplayName("구독 유효성 확인 - 만료된 구독")
    fun `구독 유효성 확인 - 만료된 구독`() {
        // 어제 만료된 구독
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now().minusMonths(2),
            expDate = LocalDate.now().minusDays(1)
        )

        assertFalse(subscription.isActive())
    }

    @Test
    @DisplayName("구독 기간 업데이트 테스트")
    fun `구독 기간 업데이트 테스트`() {

        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now().minusMonths(1),
            expDate = LocalDate.now().minusDays(1)
        )
        val newSubDate = LocalDate.now()
        val newExpDate = LocalDate.now().plusMonths(1)

        subscription.updatePeriod(newSubDate, newExpDate)

        assertEquals(newSubDate, subscription.subDate)
        assertEquals(newExpDate, subscription.expDate)
    }

    @Test
    @DisplayName("Entity toString 테스트")
    fun `Entity toString 테스트`() {
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.of(2025, 1, 1),
            expDate = LocalDate.of(2025, 2, 1)
        )

        val string = subscription.toString()

        assertNotNull(string)
        assertTrue(string.contains("Subscription"))
        assertTrue(string.contains("userId=1"))
        assertTrue(string.contains("2025-01-01"))
        assertTrue(string.contains("2025-02-01"))
    }

    @Test
    @DisplayName("구독 생성 후 즉시 활성 상태 확인")
    fun `구독 생성 후 즉시 활성 상태 확인`() {
        val subscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1)
        )

        assertTrue(subscription.isActive())
        assertFalse(subscription.canceled)
        assertNull(subscription.id) // DB에 저장되기 전이므로 ID는 null
    }


}