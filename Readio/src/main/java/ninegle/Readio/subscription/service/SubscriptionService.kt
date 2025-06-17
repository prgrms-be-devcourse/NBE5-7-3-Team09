package ninegle.Readio.subscription.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto
import ninegle.Readio.subscription.mapper.SubscriptionMapper
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.subscription.service.SubscriptionManager
import ninegle.Readio.user.service.UserContextService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// 구독 관련 API 요청을 처리하는 서비스
@Service
class SubscriptionService(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionMapper: SubscriptionMapper,
    private val userContextService: UserContextService,
    private val subscriptionManager: SubscriptionManager
) {

    companion object {
        // 비즈니스 규칙: 구독 타입은 하나만 있고 ID는 항상 1 (고정값)
        private const val SUBSCRIPTION_ID = 1L
    }

    // 현재 사용자의 구독정보를 조회
    @Transactional(readOnly = true)
    fun getSubscription(): SubscriptionResponseDto? {
        // 현재 로그인한 사용자 ID 가져오기 - 기존 프로퍼티 사용
        val userId = userContextService.currentUserId
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 구독 정보 조회 후 DTO로 변환 (구독이 없으면 null 반환)
        return subscriptionRepository.findByUserId(userId)
            ?.let { subscriptionMapper.toDto(it) }
    }

    // 구독 신청 처리(신규 구독 또는 만료된 구독 갱신)
    @Transactional
    fun createSubscription() {
        // 현재 로그인한 사용자 ID 가져오기 - 기존 프로퍼티 사용
        val userId = userContextService.currentUserId
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 구독 처리 로직을 매니저에게 위임
        subscriptionManager.subscribe(userId)
    }

    // 구독 취소 처리(구독을 즉시 종료하는 것이 아니라 갱신되지 않도록 설정)
    @Transactional
    fun cancelSubscription(subscriptionId: Long) {
        // 요청된 구독 ID가 유효한지 확인 (무조건 1이어야 함)
        if (subscriptionId != SUBSCRIPTION_ID) {
            throw BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND)
        }

        // 현재 로그인한 사용자 ID 가져오기 - 기존 프로퍼티 사용
        val userId = userContextService.currentUserId
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 실제로 사용자가 구독을 가지고 있는지 확인
        val hasSubscription = subscriptionRepository.findByUserId(userId) != null
        if (!hasSubscription) {
            throw BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND)
        }

        // 구독 취소 로직을 subscriptionManager에서 처리하도록 위임
        subscriptionManager.cancelSubscription(userId)
    }
}