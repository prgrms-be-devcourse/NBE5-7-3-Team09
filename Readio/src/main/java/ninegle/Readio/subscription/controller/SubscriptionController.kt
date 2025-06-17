package ninegle.Readio.subscription.controller

import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto
import ninegle.Readio.subscription.service.SubscriptionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscriptions")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    /**
     * 사용자의 구독 정보를 조회
     * 구독 정보가 존재하면 데이터와 함께 메시지를 반환하고,
     * 존재하지 않으면 메시지만 반환
     */
    @GetMapping
    fun getSubscription(): ResponseEntity<BaseResponse<SubscriptionResponseDto>> {
        val response = subscriptionService.getSubscription()

        return if (response != null) {
            BaseResponse.ok("조회에 성공하였습니다.", response, HttpStatus.OK)
        } else {
            BaseResponse.ok("존재하는 구독이 없습니다.", null, HttpStatus.OK)
        }
    }

    /**
     * 사용자의 새로운 구독을 생성
     */
    @PostMapping
    fun createSubscription(): ResponseEntity<BaseResponse<Void>> {
        subscriptionService.createSubscription()
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }

    /**
     * 특정 구독 ID를 취소
     * @param subscriptionId 취소할 구독의 ID
     */
    @DeleteMapping("/{subscription_id}")
    fun cancelSubscription(
        @PathVariable("subscription_id") subscriptionId: Long
    ): ResponseEntity<BaseResponse<Void>> {
        subscriptionService.cancelSubscription(subscriptionId)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }
}