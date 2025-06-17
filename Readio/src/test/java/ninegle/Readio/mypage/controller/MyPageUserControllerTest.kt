package ninegle.Readio.mypage.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.mypage.dto.request.UserUpdateRequestDto
import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.mypage.service.MyPageUserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(MyPageUserController::class)
class MyPageUserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var myPageUserService: MyPageUserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("GET /user/my - 사용자 정보 조회 성공")
    @WithMockUser
    fun `사용자 정보 조회 성공하면 응답을 200 OK로 하는지 테스트`() {
        val userInfoDto = UserInfoDto(
            email = "test@example.com",
            nickname = "팀9글",
            phoneNumber = "010-1234-5678",
            point = 15000L
        )
        given(myPageUserService.getUserInfo()).willReturn(userInfoDto)

        mockMvc.perform(get("/user/my"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("회원 정보 조회 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.email").value("test@example.com"))
            .andExpect(jsonPath("$.data.nickname").value("팀9글"))
            .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
            .andExpect(jsonPath("$.data.point").value(15000))
    }

    @Test
    @DisplayName("GET /user/my - 사용자를 찾을 수 없음")
    @WithMockUser
    fun `사용자를 찾을 수 없어서 정보 조회 실패할 경우 404로 응답하는지 테스트`() {
        given(myPageUserService.getUserInfo())
            .willThrow(BusinessException(ErrorCode.USER_NOT_FOUND))

        mockMvc.perform(get("/user/my"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
    }

    @Test
    @DisplayName("POST /user/my - 사용자 정보 수정 성공")
    @WithMockUser
    fun `사용자 정보 수정 성공하면 응답을 200 OK로 하는지 테스트`() {
        val requestDto = UserUpdateRequestDto(
            nickname = "new팀9글",
            phoneNumber = "010-1234-5678"
        )
        val updatedUserInfo = UserInfoDto(
            email = "test@example.com",
            nickname = "new팀9글",
            phoneNumber = "010-1234-5678",
            point = 1000L
        )
        given(myPageUserService.updateUserInfo(requestDto)).willReturn(updatedUserInfo)

        mockMvc.perform(post("/user/my")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("회원 정보 수정 성공"))
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.data.nickname").value("new팀9글"))
            .andExpect(jsonPath("$.data.phoneNumber").value("010-1234-5678"))
    }

    @Test
    @DisplayName("POST /user/my - 사용자를 찾을 수 없음")
    @WithMockUser
    fun `사용자를 찾을 수 없어서 정보 수정 실패할 경우 404로 응답하는지 테스트`() {
        val requestDto = UserUpdateRequestDto(
            nickname = "new팀9글",
            phoneNumber = "010-1234-5678"
        )
        given(myPageUserService.updateUserInfo(requestDto))
            .willThrow(BusinessException(ErrorCode.USER_NOT_FOUND))

        mockMvc.perform(post("/user/my")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
    }

    @Test
    @DisplayName("CSRF 없이 POST 요청 시 403 에러")
    @WithMockUser
    fun `CSRF 없이 POST 요청 시 403 에러`() {
        val requestDto = UserUpdateRequestDto(
            nickname = "팀9글",
            phoneNumber = "010-1111-1111"
        )

        mockMvc.perform(post("/user/my") // CSRF 토큰 없음
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("인증 없이 GET 요청하면 401 반환")
    fun `인증 없이 GET 요청 시 401`() {
        mockMvc.perform(get("/user/my"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("인증 없이 POST 요청하면 403 반환")
    fun `인증 없이 POST 요청 시 403`() {
        val requestDto = UserUpdateRequestDto(
            nickname = "팀9글",
            phoneNumber = "010-1111-2222"
        )

        mockMvc.perform(post("/user/my")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isForbidden)
    }

}