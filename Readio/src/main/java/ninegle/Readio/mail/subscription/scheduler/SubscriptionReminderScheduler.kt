package ninegle.Readio.mail.subscription.scheduler

import java.time.LocalDate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import ninegle.Readio.mail.subscription.service.SubscriptionMailSender
import ninegle.Readio.subscription.repository.SubscriptionRepository
import ninegle.Readio.user.repository.UserRepository

@Service
class SubscriptionReminderScheduler(
    private val subscriptionRepository: SubscriptionRepository,
    private val userRepository: UserRepository,
    private val mailSender: SubscriptionMailSender
) {
    private val log = LoggerFactory.getLogger(SubscriptionReminderScheduler::class.java)

    /**
     * 매일 오전 10시에 실행
     * 구독 종료 1일 전 또는 당일인 사용자에게 메일 발송
     */
    //@Scheduled(cron = "0 * * * * *") // 발표 시연용: 매 분 실행
    @Scheduled(cron = "0 0 10 * * *") // 매일 오전 10시
    fun sendSubscriptionReminders() {
        val today = LocalDate.now()

        val subscriptions = subscriptionRepository.findAll().stream()
            .filter { sub ->
                sub.expDate.isEqual(today) || sub.expDate.isEqual(today.plusDays(1))
            }
            .toList()

        subscriptions.forEach { sub ->
            userRepository.findById(sub.userId).ifPresent { user ->
                if (sub.expDate.isEqual(today)) {
                    mailSender.sendExpirationTodayMail(user, sub) // 종료 당일
                } else {
                    mailSender.sendExpirationSoonMail(user, sub) // 하루 전
                }
            }
        }

        log.info("구독 만료 알림 메일 작업 완료: 총 {}건", subscriptions.size)
    }
}