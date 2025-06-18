package ninegle.Readio.book.controller

import java.math.BigDecimal
import java.time.LocalDate

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import org.springframework.security.test.context.support.WithMockUser

import com.fasterxml.jackson.databind.ObjectMapper

import io.mockk.every

import ninegle.Readio.book.controller.ReviewController
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.book.dto.reviewdto.ReviewResponseDto
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.service.ReviewService
import ninegle.Readio.user.service.UserContextService

import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.dto.reviewdto.ReviewSummaryDto
import ninegle.Readio.book.util.*
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import ninegle.Readio.user.util.UserUtil
import org.mockito.Mockito.doNothing
import org.mockito.kotlin.given
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import java.util.*


@WebMvcTest(ReviewController::class)
@WithMockUser(roles = ["USER"])
class ReviewControllerTests {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userContextService: UserContextService

    @MockBean
    lateinit var reviewService: ReviewService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var blackListRepository: BlackListRepository

    lateinit var book: Book
    lateinit var user: User


    @Test
    fun `reveiew성공저장`(){
        val userId=1L
        val bookId=1L
        val dto = ReviewRequestDto(BigDecimal(3.0),"good book")
        given(userContextService.currentUserId).willReturn(userId)
        doNothing().`when`(reviewService).save(userId, dto, bookId)

        mockMvc.perform(
            post("/books/$bookId/reviews")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
    }
    @Test
    fun `review 삭제`(){
        val bookId = 1L
        val reviewId = 1L
        doNothing().`when`(reviewService).delete(reviewId)

        mockMvc.perform(
            delete("/books/$bookId/reviews/$reviewId")
                .with(csrf())
        )
            .andExpect(status().isNoContent)

    }

    @Test
    fun `review리스트 반환시 올바르지 않은 pagenation 값입력시 오류`(){
        val bookId = 1L

        mockMvc.perform(
            get("/books/$bookId/reviews")
                .with(csrf())
                .param("page", "-1")
                .param("size", "0")
        )
            .andExpect(status().isBadRequest)
    }

}