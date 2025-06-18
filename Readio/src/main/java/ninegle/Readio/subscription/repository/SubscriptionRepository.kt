package ninegle.Readio.subscription.repository

import ninegle.Readio.subscription.domain.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Long> {
    // 사용자 ID로 구독 조회 메서드
    fun findByUserId(userId: Long): Subscription?

    fun findAllByUserId(userId: Long): List<Subscription>
}