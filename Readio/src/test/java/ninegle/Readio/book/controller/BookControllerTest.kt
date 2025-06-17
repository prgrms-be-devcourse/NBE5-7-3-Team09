package ninegle.Readio.book.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.booksearch.BookListResponseDto
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
import ninegle.Readio.book.mapper.toAuthorDto
import ninegle.Readio.book.mapper.toCategoryDto
import ninegle.Readio.book.mapper.toPublisherDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.book.util.genAuthor
import ninegle.Readio.book.util.genPaginationDto
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test

@WebMvcTest(BookController::class)
@WithMockUser(roles = ["USER"])
class BookControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var service: BookService

    @Test
    fun `도서 상세 조회 요청시 성공적으로 수행되었을 때 조회된 데이터와 200 Ok응답을 반환한다`() {

        // given
        val expectedId = 1L

        val expected = BookResponseDto(
            id = expectedId,
            name = "코틀린 인 액션",
            description = "책 설명입니다.",
            image = "image.jpg",
            isbn = "1234567890",
            ecn = null,
            pubDate = LocalDate.of(2022, 1, 1),
            category = genCategory(1L, "총류", "총류 일반").toCategoryDto(),
            publisher = genPublisher(1L, "한빛미디어").toPublisherDto(),
            author = genAuthor(1L, "김영학").toAuthorDto()
        )

        // when
        `when`(service.getBookDetail(expectedId)).thenReturn(expected)

        // then
        mockMvc.get("/books/$expectedId")
        .andExpect {
            status { isOk() }
            jsonPath("$.status") { value(HttpStatus.OK.value()) }
            jsonPath("$.data.id") { value(expectedId) }
            jsonPath("$.data.name") { value(expected.name) }
        }
        .andDo { print() }
    }

    @Test
    fun `도서 상세 조회 요청시 도서 데이터가 존재하지 않을 경우 BusinessException이 발생한다`(){

        // given
        val expectedId = 1000L

        // when
        `when`(service.getBookDetail(expectedId)).thenThrow(BusinessException(ErrorCode.BOOK_NOT_FOUND))

        // then
        mockMvc.get("/books/$expectedId")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(HttpStatus.NOT_FOUND.value()) }
                jsonPath("$.code") { value(ErrorCode.BOOK_NOT_FOUND.name) }
                jsonPath("$.message") { value(ErrorCode.BOOK_NOT_FOUND.message) }
                jsonPath("$.path") { value("GET /books/$expectedId") }
            }
            .andDo { print() }

    }

    @Test
    fun `카테고리 별 조회 요청시 성공적으로 수행되었을 때 조회된 데이터, 페이지 정보, 200 OK를 응답한다 `() {

        val category = genCategory(1L, "총류", "총류 일반")
        val page = 1
        val size = 20

        val books = mutableListOf(
            BookSearchResponseDto(
                id = 1L,
                name = "코틀린 인 액션",
                image = "image1.jpg",
                categoryMajor = category.major,
                categorySub = category.sub,
                authorName = "김영학",
                rating = BigDecimal("4.5")
            ),BookSearchResponseDto(
                id = 2L,
                name = "코틀린의 정석",
                image = "image2.jpg",
                categoryMajor = category.major,
                categorySub = category.sub,
                authorName = "김영학",
                rating = BigDecimal("4.3")
            ),BookSearchResponseDto(
                id = 3L,
                name = "코틀린",
                image = "image3.jpg",
                categoryMajor = category.major,
                categorySub = category.sub,
                authorName = "김영학",
                rating = BigDecimal("4.1")
            ),
        )

        val pagination = genPaginationDto(books.size.toLong(), page, size)

        val expected = BookListResponseDto(books, pagination)


        `when`(service.getBookByCategory(category.major, page, size))
            .thenReturn(expected)

        mockMvc.get("/books?category_major=${category.major}&page=${page}&size=${size}")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(HttpStatus.OK.value()) }
                jsonPath("$.data.books[0].id") { value(books.first().id) }
                jsonPath("$.data.books[0].name") { value(books.first().name) }
                jsonPath("$.data.books[0].categorySub") { value(books.first().categorySub) }
                jsonPath("$.data.books[0].categoryMajor") { value(books.first().categoryMajor) }
                jsonPath("$.data.pagination.totalElements") { value(pagination.totalElements) }
                jsonPath("$.message") { value("카테고리별 조회가 정상적으로 수행되었습니다.")}
            }
            .andDo { print() }
    }

    @Test
    fun `카테고리 별 조회 요청시 데이터가 존재하지 않을 경우 빈 리스트, 페이지 정보, 200 OK를 응답한다`() {
        val category = genCategory(1L, "총류", "총류 일반")
        val page = 1
        val size = 20

        val books = mutableListOf<BookSearchResponseDto>()
        val pagination = genPaginationDto(books.size.toLong(), page, size)

        val expected = BookListResponseDto(books, pagination)

        `when`(service.getBookByCategory(category.major, page, size))
            .thenReturn(expected)

        mockMvc.get("/books?category_major=${category.major}&page=${page}&size=${size}")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.books") { value(expected.books) }
                jsonPath("$.data.pagination.totalElements") { value(0)}
            }
    }

    @Test
    fun `카테고리 별 조회 요쳥시 전체 조회할 경우 모든 책, 페이지 정보, 200 OK를 응답한다`() {
        val category = genCategory(1L, "총류", "총류 일반")
        val page = 1
        val size = 20

        val books = mutableListOf(
            BookSearchResponseDto(
                id = 1L,
                name = "코틀린 인 액션",
                image = "image1.jpg",
                categoryMajor = category.major,
                categorySub = category.sub,
                authorName = "김영학",
                rating = BigDecimal("4.5")
            ),BookSearchResponseDto(
                id = 2L,
                name = "코틀린의 정석",
                image = "image2.jpg",
                categoryMajor = "종교",
                categorySub = "불교",
                authorName = "김영학",
                rating = BigDecimal("4.3")
            ),BookSearchResponseDto(
                id = 3L,
                name = "코틀린",
                image = "image3.jpg",
                categoryMajor = "철학",
                categorySub = "형이상학",
                authorName = "김영학",
                rating = BigDecimal("4.1")
            ),
        )

        val pagination = genPaginationDto(books.size.toLong(), page, size)

        val expected = BookListResponseDto(books, pagination)

        `when`(service.getBookByCategory("null", page, size))
            .thenReturn(expected)

        mockMvc.get("/books?category_major=&page=${page}&size=${size}")
            .andExpect {
                status { isOk() }
                jsonPath("$.status") { value(HttpStatus.OK.value()) }
                jsonPath("$.data.books[0].id") { value(books.first().id) }
                jsonPath("$.data.books[0].name") { value(books.first().name) }
                jsonPath("$.data.books[0].categorySub") { value(books.first().categorySub) }
                jsonPath("$.data.books[0].categoryMajor") { value(books.first().categoryMajor) }
                jsonPath("$.data.pagination.totalElements") { value(pagination.totalElements) }
                jsonPath("$.message") { value("카테고리별 조회가 정상적으로 수행되었습니다.")}
            }
            .andDo { print() }
    }

    @Test
    fun `도서 검색 요청시 성공적으로 수행되었을 때 조회된 데이터와 200 Ok를 응답한다`() {
        val category = genCategory(1L, "총류", "총류 일반")
        val page = 1
        val size = 20
        val keyword = "코틀린"

        val books = mutableListOf(
            BookSearchResponseDto(
                id = 1L,
                name = "코틀린 인 액션",
                image = "image1.jpg",
                categoryMajor = category.major,
                categorySub = category.sub,
                authorName = "김영학",
                rating = BigDecimal("4.5")
            ),BookSearchResponseDto(
                id = 2L,
                name = "코틀린의 정석",
                image = "image2.jpg",
                categoryMajor = "종교",
                categorySub = "불교",
                authorName = "김영학",
                rating = BigDecimal("4.3")
            ),BookSearchResponseDto(
                id = 3L,
                name = "코틀린",
                image = "image3.jpg",
                categoryMajor = "철학",
                categorySub = "형이상학",
                authorName = "김영학",
                rating = BigDecimal("4.1")
            ),
        )

        val pagination = genPaginationDto(books.size.toLong(), page, size)

        val expected = BookListResponseDto(books, pagination)

        `when`(service.searchBooks(keyword, page, size))
            .thenReturn(expected)

        mockMvc.get("/books/search?keyword=${keyword}&page=${page}&size=${size}")
            .andExpect {
                status { isOk() }
                jsonPath("$.message") { value("검색 결과입니다.") }
                jsonPath("$.data.books[0].id") { value(books.first().id) }
                jsonPath("$.data.books[0].name") { value(books.first().name) }
                jsonPath("$.data.books[0].categorySub") { value(books.first().categorySub) }
                jsonPath("$.data.books[0].categoryMajor") { value(books.first().categoryMajor) }
                jsonPath("$.data.pagination.totalElements") { value(pagination.totalElements) }
                jsonPath("$.data.pagination.totalPages") { value(pagination.totalPages) }
            }

    }



}