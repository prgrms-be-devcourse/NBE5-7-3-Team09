package ninegle.Readio.mypage.controller

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.mypage.service.PointService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(PointController::class)
class PointControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var pointService: PointService

    @Test
    @DisplayName("GET /user/my/points - 포인트 조회 성공")
    @WithMockUser
    fun `포인트 조회 성공하면 응답을 200 OK로 하는지 테스트`() {
        val pointResponseDto = PointResponseDto(
            currentPoint = 15000L
        )
        given(pointService.getUserPoints()).willReturn(pointResponseDto)

        mockMvc.perform(get("/user/my/points"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("포인트 조회 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.currentPoint").value(15000))
    }

    @Test
    @DisplayName("GET /user/my/points - 포인트가 0인 경우")
    @WithMockUser
    fun `포인트가 0인 경우에도 정상적으로 조회되는지 테스트`() {
        val pointResponseDto = PointResponseDto(
            currentPoint = 0L
        )
        given(pointService.getUserPoints()).willReturn(pointResponseDto)

        mockMvc.perform(get("/user/my/points"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("포인트 조회 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.currentPoint").value(0))
    }

    @Test
    @DisplayName("GET /user/my/points - 높은 포인트 값 조회")
    @WithMockUser
    fun `큰 포인트 값도 정상적으로 조회되는지 테스트`() {
        val pointResponseDto = PointResponseDto(
            currentPoint = 999999L
        )
        given(pointService.getUserPoints()).willReturn(pointResponseDto)

        mockMvc.perform(get("/user/my/points"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("포인트 조회 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.currentPoint").value(999999))
    }

    @Test
    @DisplayName("GET /user/my/points - 사용자를 찾을 수 없음")
    @WithMockUser
    fun `사용자를 찾을 수 없어서 포인트 조회 실패할 경우 404로 응답하는지 테스트`() {
        given(pointService.getUserPoints())
            .willThrow(BusinessException(ErrorCode.USER_NOT_FOUND))

        mockMvc.perform(get("/user/my/points"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
    }

    @Test
    @DisplayName("인증 없이 GET 요청하면 401 반환")
    fun `인증 없이 GET 요청 시 401`() {
        mockMvc.perform(get("/user/my/points"))
            .andExpect(status().isUnauthorized)
    }
}