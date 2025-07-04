package ninegle.Readio.book.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.book.dto.viewer.ViewerResponseDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import reactor.core.publisher.Mono.`when`
import kotlin.test.Test

@WebMvcTest(ViewerController::class)
class ViewerControllerTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var service: BookService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var blackListRepository: BlackListRepository

    @Test
    @WithMockUser(roles = ["USER"])
    fun `도서 뷰어 요청이 성공적으로 수행되었을 때`() {

        // given
        val expectedId = 1L
        val epubUrl = "http://ncpAddress/book/코틀린.epub"

        val expected = ViewerResponseDto(epubUrl)

        // when
        `when`(service.getViewerBook(expectedId)).thenReturn(expected)

        // then
        mockMvc.get("/viewer/books/$expectedId")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(200) }
                jsonPath("$.data") {epubUrl}
            }
            .andDo { print() }
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `도서 뷰어 요청시 도서 데이터가 존재하지 않을 경우 BusinessException이 발생한다`() {

        // given
        val expectedId = 1000L

        `when`(service.getViewerBook(expectedId)).thenThrow(BusinessException(ErrorCode.BOOK_NOT_FOUND))

        mockMvc.get("/viewer/books/$expectedId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(HttpStatus.NOT_FOUND.value()) }
                jsonPath("$.code") { value(ErrorCode.BOOK_NOT_FOUND.name) }
                jsonPath("$.message") { value(ErrorCode.BOOK_NOT_FOUND.message) }
                jsonPath("$.path") { value("GET /viewer/books/$expectedId") }
            }
            .andDo { print() }

    }

}