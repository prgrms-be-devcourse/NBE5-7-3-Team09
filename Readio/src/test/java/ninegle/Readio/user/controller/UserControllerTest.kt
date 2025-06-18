package ninegle.Readio.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.annotation.DisplayName
import ninegle.Readio.user.dto.SignUpRequestDto
import ninegle.Readio.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType

import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@ConfigurationPropertiesScan
@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var MockMvc: MockMvc

    @Autowired
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

        verify(userService.signup(dto))

    }


}