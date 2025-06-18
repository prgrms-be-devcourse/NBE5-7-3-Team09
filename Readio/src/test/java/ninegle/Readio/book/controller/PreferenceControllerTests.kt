package ninegle.Readio.book.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.book.dto.BookIdRequestDto
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto
import ninegle.Readio.book.service.PreferenceService
import ninegle.Readio.book.service.ReviewService
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import ninegle.Readio.user.service.UserContextService
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import java.math.BigDecimal

@WebMvcTest(PreferenceController::class)
@WithMockUser(roles = ["USER"])
class PreferenceControllerTests {

 @MockBean
 lateinit var reviewService: ReviewService

 @MockBean
 lateinit var jwtTokenProvider: JwtTokenProvider

 @MockBean
 lateinit var userRepository: UserRepository

 @MockBean
 lateinit var blackListRepository: BlackListRepository
 @Autowired
 lateinit var mockMvc: MockMvc

 @Autowired
 lateinit var objectMapper: ObjectMapper

 @MockBean
 lateinit var userContextService: UserContextService

 @MockBean
 lateinit var preferenceService: PreferenceService

 @Test
 fun `save preference 성공`() {
  val userId = 1L
  val dto = BookIdRequestDto(1L)
  val responseDto = PreferenceResponseDto(
   id = 1L,
   name = "책이름",
   image = "image_url",
   rating = BigDecimal("4.5")
  )

  given(userContextService.currentUserId).willReturn(userId)
  given(preferenceService.save(userId, dto)).willReturn(responseDto)

  mockMvc.perform(
   post("/preferences")
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(dto))
    .with(csrf())
  )
   .andExpect(status().isCreated)
   .andExpect(jsonPath("$.message").value("데이터가 성공적으로 저장되었습니다."))
   .andExpect(jsonPath("$.data.id").value(responseDto.id))
   .andExpect(jsonPath("$.data.name").value(responseDto.name))
   .andExpect(jsonPath("$.data.rating").value(responseDto.rating.toDouble()))
 }

 @Test
 fun `get preferences 성공`() {
  val userId = 1L
  val page = 1
  val size = 3

  val preferences = listOf(
   PreferenceResponseDto(
    id = 1L,
    name = "책1",
    image = "img1.jpg",
    rating = BigDecimal("4.5")
   ),
   PreferenceResponseDto(
    id = 2L,
    name = "책2",
    image = "img2.jpg",
    rating = BigDecimal("4.0")
   )
  )
  val pagination = PaginationDto(
   totalElements = 2L,
   totalPages = 1,
   currentPage = page,
   size = size
  )

  val listResponseDto = PreferenceListResponseDto(
   preferences = preferences,
   pagination = pagination
  )

  given(userContextService.currentUserId).willReturn(userId)
  given(preferenceService.getPreferenceList(userId, page, size)).willReturn(listResponseDto)

  mockMvc.perform(
   get("/preferences")
    .param("page", page.toString())
    .param("size", size.toString())
    .with(csrf())
  )
   .andExpect(status().isOk)
   .andExpect(jsonPath("$.message").value("관심도서 조회가 성공적으로 수행되었습니다."))
   .andExpect(jsonPath("$.data.preferences.length()").value(preferences.size))
   .andExpect(jsonPath("$.data.preferences[0].name").value(preferences[0].name))
   .andExpect(jsonPath("$.data.pagination.totalElements").value(pagination.totalElements))
 }

 @Test
 fun `get preferences - 잘못된 pagination 파라미터일 경우 예외`() {
  val invalidPage = 0
  val invalidSize = 100

  given(userContextService.currentUserId).willReturn(1L)

  mockMvc.perform(
   get("/preferences")
    .param("page", invalidPage.toString())
    .param("size", "3")
    .with(csrf())
  )
   .andExpect(status().isBadRequest)

  mockMvc.perform(
   get("/preferences")
    .param("page", "1")
    .param("size", invalidSize.toString())
    .with(csrf())
  )
   .andExpect(status().isBadRequest)
 }
}
