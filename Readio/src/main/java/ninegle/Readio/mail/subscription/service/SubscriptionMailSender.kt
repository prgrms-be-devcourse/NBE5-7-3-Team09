package ninegle.Readio.mail.subscription.service

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import ninegle.Readio.mail.common.service.EmailService
import ninegle.Readio.subscription.domain.Subscription
import ninegle.Readio.user.domain.User

@Service
class SubscriptionMailSender(
    private val emailService: EmailService,
    private val templateProvider: SubscriptionMailTemplateProvider
) {
    private val log = LoggerFactory.getLogger(SubscriptionMailSender::class.java)

    @Async
    fun sendSubscribeMail(user: User, subscription: Subscription) { // 구독 결제 완료
        val subject = "[Readio] 구독 결제가 완료되었습니다."
        val body = templateProvider.buildSubscribeMailBody(user.nickname, subscription)
        emailService.send(user.email, subject, body)
    }

    @Async
    fun sendCancelMail(user: User, subscription: Subscription) { // 구독 취소
        val subject = "[Readio] 구독이 취소되었습니다."
        val body = templateProvider.buildCancelMailBody(user.nickname, subscription)
        emailService.send(user.email, subject, body)
    }

    @Async
    fun sendExpirationSoonMail(user: User, subscription: Subscription) {
        val subject = "[Readio] 구독이 곧 만료됩니다 (1일 전)"
        val body = templateProvider.buildExpirationSoonMailBody(user.nickname, subscription)
        emailService.send(user.email, subject, body)
    }

    @Async
    fun sendExpirationTodayMail(user: User, subscription: Subscription) {
        val subject = "[Readio] 구독이 오늘 만료됩니다"
        val body = templateProvider.buildExpirationTodayMailBody(user.nickname, subscription)
        emailService.send(user.email, subject, body)
    }
}