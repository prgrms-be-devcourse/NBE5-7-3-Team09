package ninegle.Readio.subscription.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.subscription.dto.response.SubscriptionResponseDto
import ninegle.Readio.subscription.service.SubscriptionService
import ninegle.Readio.user.config.JwtAuthFilter
import ninegle.Readio.user.service.JwtTokenProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(
    controllers = [SubscriptionController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [JwtAuthFilter::class]
        )
    ]
)
class SubscriptionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var subscriptionService: SubscriptionService

    @MockBean
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    @DisplayName("GET /subscriptions - 구독 조회 성공")
    @WithMockUser
    fun `구독 조회 성공하면 응답을 200 OK로 하는지 테스트`() {
        val responseDto = SubscriptionResponseDto(
            userId = 1L,
            subDate = LocalDate.now(),
            expDate = LocalDate.now().plusMonths(1),
            active = true,
            canceled = false
        )
        given(subscriptionService.getSubscription()).willReturn(responseDto)

        mockMvc.perform(get("/subscriptions"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("조회에 성공하였습니다."))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.userId").value(1L))
            .andExpect(jsonPath("$.data.active").value(true))
            .andExpect(jsonPath("$.data.canceled").value(false))
    }

    @Test
    @DisplayName("GET /subscriptions - 구독이 없을 때")
    @WithMockUser
    fun `구독 조회는 성공적으로 되지만, 구독 없을때 응답을 200 OK로 하는지 테스트`() {
        given(subscriptionService.getSubscription()).willReturn(null)

        mockMvc.perform(get("/subscriptions"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("존재하는 구독이 없습니다."))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    @DisplayName("POST /subscriptions - 구독 생성 성공")
    @WithMockUser
    fun `구독 생성 성공하면 응답을 200 OK로 하는지 테스트`() {
        willDoNothing().given(subscriptionService).createSubscription()

        mockMvc.perform(
            post("/subscriptions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(200))
    }

    @Test
    @DisplayName("POST /subscriptions - 이미 구독중인 경우")
    @WithMockUser
    fun `이미 구독중인 경우 구독생성 실패, 400응답이 뜨는지 테스트`() {
        given(subscriptionService.createSubscription())
            .willThrow(BusinessException(ErrorCode.ALREADY_SUBSCRIBED))

        mockMvc.perform(
            post("/subscriptions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("ALREADY_SUBSCRIBED"))
            .andExpect(jsonPath("$.message").value("이미 구독중인 구독권이 있습니다."))
    }

    @Test
    @DisplayName("POST /subscriptions - 포인트 부족")
    @WithMockUser
    fun `포인트가 부족해서 구독생성 실패, 400응답이 뜨는지 테스트`() {
        given(subscriptionService.createSubscription())
            .willThrow(BusinessException(ErrorCode.NOT_ENOUGH_POINTS))

        mockMvc.perform(
            post("/subscriptions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("NOT_ENOUGH_POINTS"))
            .andExpect(jsonPath("$.message").value("보유 포인트가 부족합니다."))
    }

    @Test
    @DisplayName("DELETE /subscriptions/{subscription_id} - 구독 취소 성공")
    @WithMockUser
    fun `구독 취소 성공하면 응답을 200 OK로 하는지 테스트`() {
        val subscriptionId = 1L
        willDoNothing().given(subscriptionService).cancelSubscription(subscriptionId)

        mockMvc.perform(
            delete("/subscriptions/{subscription_id}", subscriptionId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(200))
    }

    @Test
    @DisplayName("DELETE /subscriptions/{subscription_id} - 구독을 찾을 수 없음")
    @WithMockUser
    fun `존재하는 구독이 없어서 구독 취소 실패할 경우 404로 응답을 하는지 테스트`() {
        val subscriptionId = 1L
        given(subscriptionService.cancelSubscription(subscriptionId))
            .willThrow(BusinessException(ErrorCode.SUBSCRIPTION_NOT_FOUND))

        mockMvc.perform(
            delete("/subscriptions/{subscription_id}", subscriptionId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNotFound)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("SUBSCRIPTION_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("존재하지 않는 구독입니다."))
    }

    @Test
    @DisplayName("DELETE /subscriptions/{subscription_id} - 이미 취소된 구독")
    @WithMockUser
    fun `이미 취소된 구독을 또 취소 하려고 하면 구독 취소 실패가 뜨고 400으로 응답하는지 테스트`() {
        val subscriptionId = 1L
        given(subscriptionService.cancelSubscription(subscriptionId))
            .willThrow(BusinessException(ErrorCode.SUBSCRIPTION_CANCELED))

        mockMvc.perform(
            delete("/subscriptions/{subscription_id}", subscriptionId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.code").value("SUBSCRIPTION_CANCELED"))
            .andExpect(jsonPath("$.message").value("이미 취소된 구독입니다."))
    }

    @Test
    @DisplayName("CSRF 없이 POST 요청 시 403 에러")
    @WithMockUser
    fun `CSRF 없이 POST 요청 시 403 에러`() {
        mockMvc.perform(
            post("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
        ) // CSRF 토큰 없음
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("인증 없이 요청하면 401 반환")
    fun `인증 없이 요청 시 401`() {
        mockMvc.perform(
            get("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
    }
}