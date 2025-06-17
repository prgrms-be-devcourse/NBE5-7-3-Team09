
package ninegle.Readio.subscription.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mail.subscription.service.SubscriptionMailSender
import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * 구독 관련 핵심 비즈니스 로직을 처리하는 서비스
 * 구독 생성, 갱신, 취소 등의 핵심 기능을 담당
 */
@Service
class SubscriptionManager(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository,
    private val mailSender: SubscriptionMailSender
) {

    companion object {
        // 구독 비용
        private const val SUBSCRIPTION_COST = 14900L
    }

    /**
     * 구독 생성 또는 갱신 처리
     * - 새 사용자면 새 구독 생성
     * - 기존 사용자면 구독 상태에 따라 적절히 처리
     */
    @Transactional
    fun subscribe(userId: Long) {
        // 사용자 존재 여부 확인 (없으면 예외 발생)
        val user = userRepository.findById(userId).orElse(null)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 구독 정보 조회
        val existingSubscription = subscriptionRepository.findByUserId(userId)

        // 오늘 날짜와 만료일 계산 (1개월 구독)
        val now = LocalDate.now()
        val exp = now.plusMonths(1)

        // 기존 구독이 있는 경우
        if (existingSubscription != null) {
            val subscription = existingSubscription

            // 아직 유효한 구독이 있고 취소하지 않은 상태라면 - 중복 구독 불가!
            if (subscription.isActive() && !subscription.canceled) {
                throw BusinessException(ErrorCode.ALREADY_SUBSCRIBED)
            }

            // 취소된 구독인 경우
            if (subscription.canceled) {
                // 취소했지만 아직 만료일이 안 지났으면 - 재구독 불가!
                // 즉, 이전에 취소했어도 구독 기간이 남아있으면 새로 결제 못함
                if (subscription.isActive()) {
                    throw BusinessException(ErrorCode.ALREADY_SUBSCRIBED)
                }
                // 취소했고 만료일도 지났으면 - 재구독 가능!
            }

            // 포인트 차감 (부족하면 예외 발생)
            chargePoints(user)

            // 구독 기간 업데이트
            subscription.updatePeriod(now, exp)

            // 취소 상태였다면 취소 표시 해제
            if (subscription.canceled) {
                subscription.uncancel()
            }

            // 변경된 구독 정보 저장
            subscriptionRepository.save(subscription)

            // 구독 갱신 메일 전송
            mailSender.sendSubscribeMail(user, subscription)
            return
        }

        // 신규 구독자인 경우: 새 구독 생성
        chargePoints(user)
        val newSubscription = Subscription.create(
            userId = userId,
            subDate = now,
            expDate = exp
        )

        // 새 구독 정보 저장
        subscriptionRepository.save(newSubscription)

        // 신규 구독 안내 메일 전송
        mailSender.sendSubscribeMail(user, newSubscription)
    }

    /**
     * 구독 취소 처리
     * - 구독 자체는 삭제하지 않고 취소 표시만 함
     * - 취소해도 만료일까지는 서비스를 이용가능하도록 하기 위해서
     */
    @Transactional
    fun cancelSubscription(userId: Long) {
        // 사용자의 구독 정보 조회 (없으면 예외 발생)
        val subscription = subscriptionRepository.findByUserId(userId)
            ?: throw BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND)

        // 이미 취소된 구독이면 또 취소할 수 없음
        if (subscription.canceled) {
            throw BusinessException(ErrorCode.SUBSCRIPTION_CANCELED)
        }

        // 구독 취소 처리
        subscription.cancel()
        subscriptionRepository.save(subscription)

        // 사용자 정보 조회 (해당 사용자에게 메일 발송용)
        val user = userRepository.findById(userId).orElse(null)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 구독 취소 안내 메일 전송
        mailSender.sendCancelMail(user, subscription)
    }

    /**
     * 구독 결제를 위한 포인트 차감
     * - 포인트가 부족하면 결제 불가
     */
    private fun chargePoints(user: User) {
        // 포인트가 구독 비용보다 적으면 결제 불가
        if (user.point < SUBSCRIPTION_COST) {
            throw BusinessException(ErrorCode.NOT_ENOUGH_POINTS)
        }

        // 포인트 차감 - 코틀린 프로퍼티 직접 접근
        user.point = user.point - SUBSCRIPTION_COST
    }
}
