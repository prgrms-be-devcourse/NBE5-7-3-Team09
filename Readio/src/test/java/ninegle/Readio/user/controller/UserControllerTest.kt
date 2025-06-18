package ninegle.Readio.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.annotation.DisplayName
import jakarta.servlet.http.HttpServletResponse
import ninegle.Readio.mypage.controller.MyPageUserController
import ninegle.Readio.user.config.JwtAuthFilter
import ninegle.Readio.user.config.SecurityConfig
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.dto.RefreshTokenRequestDto
import ninegle.Readio.user.dto.SignUpRequestDto
import ninegle.Readio.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify




@WebMvcTest(
    controllers = [UserController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [JwtAuthFilter::class, SecurityConfig::class]
        )
    ]
)

@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc


    @MockBean
    private lateinit var userService: UserService

    @Autowired
    private lateinit var objectMapper: ObjectMapper


    @Test
    fun siginupTest(){
        val dto = SignUpRequestDto("user@email.com", "password123!", "닉네임","010-1111-1111")

        doNothing().`when`(userService).signup(dto) // 반환값 없는 메서드 처리

        mockMvc.perform(post("/user/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andDo(print())

        verify(userService).signup(dto)
    }


    @Test
    fun loginTest() {
        val dto = LoginRequestDto("user@email.com", "password123!")

    // 첫 번째 파라미터가 userDto랑 같은지 체크하고,
    // 두 번째 파라미터는 어떤 값이라도 상관없다(any())   any가 아닌 HttpServletResponse는 스프링 내부에서 생성되고 전달되는 객체라 Mockito 인식이 안됨
        doNothing().`when`(userService).login(eq(dto), any())

        mockMvc.perform(
            post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isOk)
            .andDo(print())

        verify(userService).login(eq(dto), any())
    }

    @Test
    fun logoutTest() {
        val accessToken = "Bearer access-token"
        val refreshTokenRequestDto = RefreshTokenRequestDto(refreshToken = "refresh-token")
        doNothing().`when`(userService).logout(eq(accessToken),eq(refreshTokenRequestDto))

        mockMvc.perform(
            post("/user/logout")
                .header("Authorization", "Bearer access-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequestDto))
        )
            .andExpect(status().isNoContent)
            .andDo(print())

        verify(userService).logout(eq(accessToken),eq(refreshTokenRequestDto))
    }




}