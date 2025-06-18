package ninegle.Readio.book.controller

import com.fasterxml.jackson.databind.ObjectMapper
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.author.AuthorDto
import ninegle.Readio.book.service.BookService
import ninegle.Readio.book.util.genBookReq
import ninegle.Readio.book.util.genMockMultipartFile
import ninegle.Readio.category.dto.CategoryDto
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.publisher.dto.PublisherDto
import ninegle.Readio.user.repository.BlackListRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.JwtTokenProvider
import org.mockito.Mockito.`when`
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.awt.PageAttributes

import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

@WebMvcTest(AdminBookController::class)
@WithMockUser(roles = ["ADMIN"])
class AdminBookControllerTests {

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

    @Autowired
    lateinit var om: ObjectMapper

    val epubFile = genMockMultipartFile(
    name = "epubFile",
    originalFilename = "test.epub",
    contentType = "application/epub+zip",
    content = "test".toByteArray()
    )

    val image = genMockMultipartFile(
    name = "image",
    originalFilename = "test.jpg",
    contentType = "image/jpeg",
    content = "test".toByteArray()
    )

    @Test
    fun `도서 저장시 성공적으로 수행되었을 때 201 CREATED 응답을 반환한다`() {

        val request = genBookReq(
            name = "코틀린 인 액션",
            description = "코틀린에 대한 설명입니다.",
            image = image,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = epubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김영학"
        )

        doNothing().`when`(service).save(request)

        mockMvc.perform(
            multipart(HttpMethod.POST,"/admin/books")
                .file(image)
                .file(epubFile)
                .param("name", request.name)
                .param("description", request.description)
                .param("isbn", request.isbn)
                .param("ecn", request.ecn)
                .param("pubDate", request.pubDate.toString())
                .param("categorySub", request.categorySub)
                .param("publisherName", request.publisherName)
                .param("authorName", request.authorName)
                .with(csrf())
        ).andExpect {
            status().isCreated
        }
    }

    @Test
    fun `도서 수정시 성공적으로 수행되었을 때 수정된 데이터, 메시지, 200 OK를 응답한다`() {

        val id = 1L

        val request = genBookReq(
            name = "코틀린 인 액션",
            description = "코틀린에 대한 설명입니다.",
            image = image,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = epubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김영학"
        )

        val response = BookResponseDto(
            id = id,
            name = request.name,
            description = request.description,
            image = "image/${request.name}.jpg",
            isbn = request.isbn,
            ecn = request.ecn,
            pubDate = request.pubDate,
            category = CategoryDto(1L, "철학", "형이상학"),
            publisher = PublisherDto(1L, "한빛미디어"),
            author = AuthorDto(1L, "김영학")
        )

        `when`(service.updateBook(id, request)).thenReturn(response)

        mockMvc.perform(
            multipart(HttpMethod.PUT,"/admin/books/$id")
                .file(image)
                .file(epubFile)
                .param("name", request.name)
                .param("description", request.description)
                .param("isbn", request.isbn)
                .param("ecn", request.ecn)
                .param("pubDate", request.pubDate.toString())
                .param("categorySub", request.categorySub)
                .param("publisherName", request.publisherName)
                .param("authorName", request.authorName)
                .with(csrf())
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
            .andExpect(jsonPath("$.data.id").value(id))
            .andExpect(jsonPath("$.data.name").value(request.name))
            .andExpect(jsonPath("$.data.description").value(request.description))
            .andExpect(jsonPath("$.data.image").value("image/${request.name}.jpg"))
            .andDo(print())
    }

    @Test
    fun `도서 수정시 해당 id에 데이터가 존재하지 않을 경우 404 반환 `() {
        val id = 1000L

        val request = genBookReq(
            name = "코틀린 인 액션",
            description = "코틀린에 대한 설명입니다.",
            image = image,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = epubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김영학",

        )

        `when`(service.updateBook(id, request)).thenThrow(BusinessException(ErrorCode.BOOK_NOT_FOUND))

        mockMvc.perform(
            multipart(HttpMethod.PUT,"/admin/books/$id")
                .file(image)
                .file(epubFile)
                .param("name", request.name)
                .param("description", request.description)
                .param("isbn", request.isbn)
                .param("ecn", request.ecn)
                .param("pubDate", request.pubDate.toString())
                .param("categorySub", request.categorySub)
                .param("publisherName", request.publisherName)
                .param("authorName", request.authorName)
                .with(csrf())

        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.code").value(ErrorCode.BOOK_NOT_FOUND.name))
            .andExpect(jsonPath("$.message").value(ErrorCode.BOOK_NOT_FOUND.message))
            .andExpect(jsonPath("$.path").value("PUT /admin/books/$id"))
            .andDo(print())
    }

    @Test
    fun `도서 삭제시 성공적으로 수행되었을 때 200 OK를 응답한다`() {
        val id = 1L

        doNothing().`when`(service).deleteBook(id)

        mockMvc.delete("/admin/books/$id") {
            content = om.writeValueAsString(id)
            with(csrf())
        }
            .andExpect {
                status { isOk() }
            }
            .andDo{ print() }
    }

    @Test
    fun `도서 삭제시 해당 id에 데이터가 존재하지 않을 경우 404반환`() {
        val id = 1000L

        `when`(service.deleteBook(id)).thenThrow(BusinessException(ErrorCode.BOOK_NOT_FOUND))

        mockMvc.delete("/admin/books/$id") {
            with(csrf())
        }
            .andExpect {
                status { isNotFound() }
                jsonPath("$.code") { value(ErrorCode.BOOK_NOT_FOUND.name) }
                jsonPath("$.message") { value(ErrorCode.BOOK_NOT_FOUND.message) }
                jsonPath("$.path") { value("DELETE /admin/books/$id") }
            }
            .andDo { print() }
    }

}