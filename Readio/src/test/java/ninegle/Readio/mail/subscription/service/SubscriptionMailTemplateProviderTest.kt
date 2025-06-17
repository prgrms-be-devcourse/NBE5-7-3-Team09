package ninegle.Readio.mail.subscription.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import ninegle.Readio.subscription.domain.Subscription
import java.time.LocalDate

class SubscriptionMailTemplateProviderTest {

    private lateinit var templateProvider: SubscriptionMailTemplateProvider
    private lateinit var testSubscription: Subscription

    @BeforeEach
    fun setUp() {
        templateProvider = SubscriptionMailTemplateProvider()

        testSubscription = Subscription.create(
            userId = 1L,
            subDate = LocalDate.of(2025, 6, 1),
            expDate = LocalDate.of(2025, 7, 1)
        )
    }

    @Test
    fun `구독 결제 완료 메일 템플릿 생성 - 정상 닉네임`() {
        val nickname = "홍길동"

        val result = templateProvider.buildSubscribeMailBody(nickname, testSubscription)

        assertThat(result).isNotNull()
        assertThat(result).contains("안녕하세요, 홍길동님.")
        assertThat(result).contains("홍길동님께서 구독을 완료해 주셨습니다.")
        assertThat(result).contains("구독 시작일: 2025-06-01")
        assertThat(result).contains("구독 종료일: 2025-07-01")
        assertThat(result).contains("총 결제 금액: 14,900원")
        assertThat(result).contains("Readio 팀 드림")
    }

    @Test
    fun `구독 결제 완료 메일 템플릿 생성 - 빈 닉네임`() {
        val emptyNickname = ""

        val result = templateProvider.buildSubscribeMailBody(emptyNickname, testSubscription)

        assertThat(result).contains("안녕하세요, 회원님.")
        assertThat(result).contains("회원님께서 구독을 완료해 주셨습니다.")
    }

    @Test
    fun `구독 취소 메일 템플릿 생성 - 정상 케이스`() {
        val nickname = "김철수"

        val result = templateProvider.buildCancelMailBody(nickname, testSubscription)

        assertThat(result).isNotNull()
        assertThat(result).contains("안녕하세요, 김철수님.")
        assertThat(result).contains("김철수님의 구독이 성공적으로 취소되었습니다.")
        assertThat(result).contains("종료일: 2025-07-01")
        assertThat(result).contains("Readio 팀 드림")
        assertThat(result).doesNotContain("구독 시작일")
    }

    @Test
    fun `구독 취소 메일 템플릿 생성 - 빈 닉네임`() {
        val emptyNickname = ""

        val result = templateProvider.buildCancelMailBody(emptyNickname, testSubscription)

        assertThat(result).contains("안녕하세요, 회원님.")
        assertThat(result).contains("회원님의 구독이 성공적으로 취소되었습니다.")
    }

    @Test
    fun `구독 만료 임박 메일 템플릿 생성 - 정상 케이스`() {
        val nickname = "고길동"

        val result = templateProvider.buildExpirationSoonMailBody(nickname, testSubscription)

        assertThat(result).isNotNull()
        assertThat(result).contains("안녕하세요, 고길동님.")
        assertThat(result).contains("회원님의 구독이 내일인 2025-07-01에 만료됩니다.")
        assertThat(result).contains("구독 종료일: 2025-07-01")
        assertThat(result).contains("구독 갱신을 권장드립니다.")
        assertThat(result).contains("Readio 팀 드림")
    }

    @Test
    fun `구독 만료 당일 메일 템플릿 생성 - 정상 케이스`() {
        val nickname = "홍길동"

        val result = templateProvider.buildExpirationTodayMailBody(nickname, testSubscription)

        assertThat(result).isNotNull()
        assertThat(result).contains("안녕하세요, 홍길동님.")
        assertThat(result).contains("회원님의 구독이 오늘인 2025-07-01에 만료됩니다.")
        assertThat(result).contains("구독 종료일: 2025-07-01")
        assertThat(result).contains("구독 갱신을 추천드립니다.")
        assertThat(result).contains("Readio 팀 드림")
    }

    @Test
    fun `다양한 날짜 형식으로 템플릿 생성 테스트`() {
        val nickname = "테스터"
        val subscriptionWithDifferentDates = Subscription.create(
            userId = 1L,
            subDate = LocalDate.of(2025, 12, 25),
            expDate = LocalDate.of(2026, 1, 25)
        )

        val subscribeResult = templateProvider.buildSubscribeMailBody(nickname, subscriptionWithDifferentDates)
        val cancelResult = templateProvider.buildCancelMailBody(nickname, subscriptionWithDifferentDates)

        assertThat(subscribeResult).contains("구독 시작일: 2025-12-25")
        assertThat(subscribeResult).contains("구독 종료일: 2026-01-25")
        assertThat(cancelResult).contains("종료일: 2026-01-25")
    }

    @Test
    fun `모든 템플릿에 공통 요소 포함 확인`() {
        val nickname = "공통테스터"

        val subscribeResult = templateProvider.buildSubscribeMailBody(nickname, testSubscription)
        val cancelResult = templateProvider.buildCancelMailBody(nickname, testSubscription)
        val expirationSoonResult = templateProvider.buildExpirationSoonMailBody(nickname, testSubscription)
        val expirationTodayResult = templateProvider.buildExpirationTodayMailBody(nickname, testSubscription)

        val templates = listOf(subscribeResult, cancelResult, expirationSoonResult, expirationTodayResult)

        templates.forEach { template ->
            assertThat(template).contains("Readio 팀 드림")
            assertThat(template).contains("© 2025. Readio, Inc. All rights reserved.")
            assertThat(template).contains("본 메일은 발신 전용입니다.")
            assertThat(template).contains("주식회사 Readio")
        }
    }

    @Test
    fun `특수문자 닉네임으로 템플릿 생성 테스트`() {
        val specialNickname = "테스터@#$%"

        val result = templateProvider.buildSubscribeMailBody(specialNickname, testSubscription)

        assertThat(result).contains("안녕하세요, ${specialNickname}님.")
        assertThat(result).contains("${specialNickname}님께서 구독을 완료해 주셨습니다.")
    }
}