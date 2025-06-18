package ninegle.Readio.book.service

import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import ninegle.Readio.adapter.service.NCloudStorageService
import ninegle.Readio.book.domain.Author
import ninegle.Readio.book.mapper.BookMapper
import ninegle.Readio.book.mapper.BookSearchMapper
import ninegle.Readio.book.repository.AuthorRepository
import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.book.repository.BookSearchRepository
import ninegle.Readio.book.util.genAuthor
import ninegle.Readio.book.util.genBook
import ninegle.Readio.book.util.genBookReq
import ninegle.Readio.book.util.genBookRespDto
import ninegle.Readio.book.util.genBookSearch
import ninegle.Readio.book.util.genMockMultipartFile
import ninegle.Readio.category.repository.CategoryRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.publisher.repository.PublisherRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.test.Test

class BookServiceUpdateTests {
    val bookRepository = mockk<BookRepository>()
    val authorRepository = mockk<AuthorRepository>()
    val categoryRepository = mockk<CategoryRepository>()
    val publisherRepository = mockk<PublisherRepository>()
    val bookSearchRepository = mockk<BookSearchRepository>()
    val nCloudStorageService = mockk<NCloudStorageService>()
    val bookSearchMapper = mockk<BookSearchMapper>()
    val bookMapper = mockk<BookMapper>()

    val service = BookService(
        bookRepository =  bookRepository,
        authorRepository = authorRepository,
        categoryRepository = categoryRepository,
        publisherRepository = publisherRepository,
        bookSearchRepository = bookSearchRepository,
        nCloudStorageService = nCloudStorageService,
        bookSearchMapper = bookSearchMapper,
        bookMapper = bookMapper
    )

    lateinit var mockEpubFile: MockMultipartFile
    lateinit var mockImageFile: MockMultipartFile

    @BeforeEach
    fun setUp() {
        mockEpubFile =
            genMockMultipartFile(
                name = "EpubFile",
                originalFilename = "test.epub",
                contentType = "application/epub+zip",
                content = "test".toByteArray()
            )

        mockImageFile = genMockMultipartFile(
            name = "ImageFile",
            originalFilename = "test.jpg",
            contentType = "image/jpeg",
            content = "test".toByteArray()
        )
    }

    @Test
    fun `도서 정보 업데이트 성공시 BookResponseDto를 반환한다`() {

        // given
        val id = 1L

        val bookTitle = "코틀린 인 액션"
        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)


        val request = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )
        val expectedImageUrl = "image/${request.name}.jpg"

        val targetBook = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl
        )

        val updateBookName = "코틀린의 정석"
        val updateIsbn = UUID.randomUUID().toString()
        val updateEcn = UUID.randomUUID().toString()

        val updateAuthorName = "코틀린"
        val updatedAuthor = Author(2L, updateAuthorName)

        val updateReq = genBookReq(
            name = updateBookName,
            description = "책 설명",
            image = mockImageFile,
            isbn = updateIsbn,
            ecn = updateEcn,
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = updateAuthorName
        )

        val targetBookSearch = genBookSearch(
            1L,
            bookTitle,
            expectedImageUrl,
            categorySub,
            categoryMajor,
            authorName,
            false,
            BigDecimal(4.5)
        )

        val updatedBook = genBook(
            id = id,
            req = updateReq,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl
        )
        val updatedBookSearch = genBookSearch(
            1L,
            updateBookName,
            expectedImageUrl,
            categorySub,
            categoryMajor,
            updateAuthorName,
            false,
            BigDecimal(4.5)
        )

        // when
        every { bookRepository.findByIdAndExpiredFalse(id) } returns targetBook
        every { bookSearchRepository.findById(id) } returns Optional.of(targetBookSearch)
        every { bookRepository.existsByIsbnAndIdNot(updateReq.isbn, id) } returns false
        every { bookRepository.existsByEcnAndIdNot(updateReq.ecn, id) } returns false

        every { nCloudStorageService.renameFileOnCloud(targetBook.name, updateBookName,"epub",".epub") } just Runs
        every { nCloudStorageService.renameFileOnCloud(targetBook.name, updateBookName,"image",".jpg") } just Runs

        every { categoryRepository.findBySub(categorySub) } returns category
        every { authorRepository.findByName(updateAuthorName) } returns updatedAuthor
        every { publisherRepository.findByName(publisherName) } returns publisher

        every { bookRepository.save(targetBook) } returns updatedBook
        every { bookSearchRepository.save(targetBookSearch) } returns updatedBookSearch
        every { bookMapper.toDto(any()) } returns genBookRespDto(updatedBook)


        val actual = service.updateBook(id, updateReq)

        // then
        actual.id shouldBe updatedBook.id
        actual.name shouldBe updatedBook.name
        actual.isbn shouldBe updatedBook.isbn
        actual.ecn shouldBe updatedBook.ecn
        actual.author.name shouldBe updatedBook.author.name

    }

    @Test
    fun `도서 정보 업데이트시 id에 해당하는 데이터가 존재하지 않을 경우 BusinessException이 발생한다`() {
        // given
        val id = 1L

        val bookTitle = "코틀린 인 액션"
        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categorySub = "형이상학"

        val request = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )

        every { bookRepository.findByIdAndExpiredFalse(id) } returns null

        val actual = assertThrows<BusinessException> { service.updateBook(id, request) }

        actual.errorCode shouldBe ErrorCode.BOOK_NOT_FOUND
        actual.message shouldBe ErrorCode.BOOK_NOT_FOUND.message
        actual.errorCode.status shouldBe ErrorCode.BOOK_NOT_FOUND.status

    }

    @Test
    fun `도서 정보 업데이트시 이미 존재하는 ISBN일 경우 BusinessException이 발생한다`() {
        // given
        val id = 1L

        val bookTitle = "코틀린 인 액션"
        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)

        val updateIsbn = UUID.randomUUID().toString()
        val updateEcn = UUID.randomUUID().toString()

        val request = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )

        val updateReq = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = updateIsbn,
            ecn = updateEcn,
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )

        val expectedImageUrl = "image/${request.name}.jpg"

        val targetBook = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl
        )

        every { bookRepository.findByIdAndExpiredFalse(id) } returns targetBook
        every { bookRepository.existsByIsbnAndIdNot(updateReq.isbn, id) } returns true

        val actual = assertThrows<BusinessException> { service.updateBook(id, updateReq) }

        actual.message shouldBe ErrorCode.DUPLICATE_ISBN.message
        actual.errorCode.status shouldBe ErrorCode.DUPLICATE_ISBN.status

    }

    @Test
    fun `도서 정보 업데이트시 이미 존재하는 Ecn일 경우 BusinessException이 발생한다`() {
        // given
        val id = 1L

        val bookTitle = "코틀린 인 액션"
        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)

        val updateIsbn = UUID.randomUUID().toString()
        val updateEcn = UUID.randomUUID().toString()

        val request = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )

        val updateReq = genBookReq(
            name = bookTitle,
            description = "책 설명",
            image = mockImageFile,
            isbn = updateIsbn,
            ecn = updateEcn,
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = categorySub,
            publisherName = publisherName,
            authorName = authorName
        )

        val expectedImageUrl = "image/${request.name}.jpg"

        val targetBook = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl
        )

        every { bookRepository.findByIdAndExpiredFalse(id) } returns targetBook
        every { bookRepository.existsByIsbnAndIdNot(updateReq.isbn, id) } returns false
        every { bookRepository.existsByEcnAndIdNot(updateReq.ecn, id) } returns true

        val actual = assertThrows<BusinessException> { service.updateBook(id, updateReq) }

        actual.message shouldBe ErrorCode.DUPLICATE_ECN.message
        actual.errorCode.status shouldBe ErrorCode.DUPLICATE_ECN.status

    }



}