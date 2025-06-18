package ninegle.Readio.book.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.domain.Review
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.book.mapper.ReviewMapper
import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.book.repository.BookSearchRepository
import ninegle.Readio.book.repository.ReviewRepository
import ninegle.Readio.book.util.*
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.service.UserService
import ninegle.Readio.user.util.UserUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class ReviewServiceSaveTests {

    val bookRepository = mockk<BookRepository>()
    val bookService = mockk<BookService>()
    val bookSearchRepository = mockk<BookSearchRepository>()
    val reviewRepository = mockk<ReviewRepository>()
    val reviewMapper = mockk<ReviewMapper>()
    val userService = mockk<UserService>()


    val service = ReviewService(
        userService = userService,
        reviewMapper = reviewMapper,
        reviewRepository = reviewRepository,
        bookSearchRepository = bookSearchRepository,
        bookService = bookService,
        bookRepository = bookRepository
    )
    lateinit var mockEpubFile: MockMultipartFile
    lateinit var mockImageFile: MockMultipartFile
    lateinit var book: Book
    lateinit var user: User
    lateinit var bookSearch : BookSearch
    @BeforeEach
    fun setUp() {
        mockEpubFile =
            genMockMultipartFile(
                name = "epubFile",
                originalFilename = "test.epub",
                contentType = "application/epub+zip",
                content = "test".toByteArray()
            )

        mockImageFile = genMockMultipartFile(
            name = "image",
            originalFilename = "test.jpg",
            contentType = "image/jpeg",
            content = "test".toByteArray()
        )

        val request = genBookReq(
            name = "test",
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김작가"
        )

        val expectedCategory = genCategory(110, "철학", request.categorySub)
        val expectedPublisher = genPublisher(1L, request.publisherName)
        val expectedAuthor = genAuthor(1L, request.authorName)


        val expectedEpubFileKey = "epub/${request.name}.epub"
        val expectedImageFileKey = "image/${request.name}.jpg"

        book = genBook(1L,request, expectedPublisher, expectedAuthor, expectedCategory, expectedImageFileKey)
        bookSearch = genBookSearch(1L, "test", expectedImageFileKey, expectedCategory.sub, expectedCategory.major, request.authorName, expired = false, rating = BigDecimal.ZERO )

        val dto = LoginRequestDto("test@example.com", "1234")
        user = UserUtil.createTestUser()
        user.id =1L
    }

    @Test
    fun `리뷰 성공등록`() {

        val reviewRequestDto = ReviewRequestDto(BigDecimal(4.0),"goodBook")

        val review = mockk<Review>()

        every { userService.getUser(1L) } returns user
        every { userService.getById(1L) } returns user
        every { bookService.getBookById(1L) } returns book
        every { bookRepository.findById(1L) } returns Optional.of(book)
        every { bookSearchRepository.findById(1L) } returns Optional.of(bookSearch)
        every { bookSearchRepository.save(bookSearch) } returns bookSearch
        every { reviewRepository.flush() } returns Unit
        every { reviewRepository.findAverageRatingByBook(book.id!!)} returns book.rating
        every { reviewMapper.toEntity(eq(reviewRequestDto), eq(user), eq(book)) } returns review
        every { reviewRepository.existsByUserAndBook(user, book) } returns false
        every { reviewRepository.save(review) } returns review

        service.save(user.id, reviewRequestDto,book.id!!)

        verify { reviewRepository.save(review) }

    }
    @Test
    fun `중복 리뷰 발견시 오류 발생`() {
        val reviewRequestDto = ReviewRequestDto(BigDecimal(4.0), "goodBook")

        every { userService.getUser(1L) } returns user
        every { bookRepository.findById(1L) } returns Optional.of(book)
        every { userService.getById(1L) } returns user
        every { bookService.getBookById(1L) } returns book
        every { reviewRepository.existsByUserAndBook(user,book) } returns true

        assertThrows<BusinessException> {
            service.save(1L, reviewRequestDto,1L)
        }


        verify(exactly = 1) { reviewRepository.existsByUserAndBook(user,book) }
    }

}