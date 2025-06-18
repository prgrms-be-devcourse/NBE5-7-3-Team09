package ninegle.Readio.book.service

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import ninegle.Readio.adapter.service.NCloudStorageService
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
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
import ninegle.Readio.book.util.genPaginationDto
import ninegle.Readio.book.util.toSearchResponseDto
import ninegle.Readio.category.repository.CategoryRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.publisher.repository.PublisherRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test

class BookServiceReadTests {

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
    fun `도서 상세 조회 성공시 BookResponseDto를 반환한다`() {

        val id = 1L

        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)


        val request = genBookReq(
            name = "test",
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

        val book = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl
        )

        val expectedBookResp = genBookRespDto(book)

        every { bookRepository.findByIdAndExpiredFalse(id) } returns book
        every { bookMapper.toDto(book) } returns expectedBookResp

        val actual = service.getBookDetail(id)

        actual.id shouldBe expectedBookResp.id
        actual.name shouldBe expectedBookResp.name
        actual.description shouldBe expectedBookResp.description
        actual.image shouldBe expectedBookResp.image
        actual.isbn shouldBe expectedBookResp.isbn
        actual.ecn shouldBe expectedBookResp.ecn
        actual.pubDate shouldBe expectedBookResp.pubDate
        actual.category shouldBe expectedBookResp.category
        actual.publisher shouldBe expectedBookResp.publisher
        actual.author shouldBe expectedBookResp.author

    }

    @Test
    fun `도서 상세 조회시 id에 해당하는 데이터가 존제하지 않을 경우 BusinessException이 발생한다`() {

        val id = 100L

        every { bookRepository.findByIdAndExpiredFalse(id) } returns null

        val actual = assertThrows<BusinessException> { service.getBookDetail(id) }

        actual.message shouldBe "해당 책을 찾을 수 없습니다."
        actual.errorCode.status shouldBe HttpStatus.NOT_FOUND
        actual.errorCode.name shouldBe "BOOK_NOT_FOUND"

    }

    @Test
    fun `도서 상세 조회시 id에 해당하는 데이터가 존재하지만 만료된 책일 경우 BusinessException이 발생한다`() {

        val id = 1L

        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)


        val request = genBookReq(
            name = "test",
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

        val book = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = expectedImageUrl,
            expired = true
        )

        every { bookRepository.findByIdAndExpiredFalse(id) } returns book

        val actual = assertThrows<BusinessException> { service.getBookDetail(id) }

        actual.errorCode.status shouldBe HttpStatus.NOT_FOUND
        actual.errorCode.name shouldBe "BOOK_NOT_FOUND"
        actual.message shouldBe "해당 책을 찾을 수 없습니다."

    }

    @Test
    fun `도서 전체 조회 성공시 BookListResponseDto를 반환한다`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val books = mutableListOf(
                genBookSearch(id = 1L,
                    name = "book1",
                    image = "image/book1.jpg",
                    categorySub = "철학",
                    categoryMajor = "형이상학",
                    author = "김영학",
                    expired = false,
                    rating = BigDecimal(4.5)
                ),genBookSearch(id = 2L,
                    name = "book2",
                    image = "image/book2.jpg",
                    categorySub = "총류",
                    categoryMajor = "도서관학",
                    author = "김영학",
                    expired = false,
                    rating = BigDecimal(4.8)
                ),genBookSearch(id = 3L,
                    name = "book3",
                    image = "image/book3.jpg",
                    categorySub = "종교",
                    categoryMajor = "불교",
                    author = "김영학",
                    expired = false,
                    rating = BigDecimal(3.8)
                ),
            )

        val expectedPage = PageImpl(books, pageable, books.size.toLong())

        every { bookSearchRepository.findByExpiredFalse(pageable) } returns expectedPage

        every {
            bookSearchMapper.toResponseDto(books)
        } returns books.map { it.toSearchResponseDto()}.toMutableList()

        every {
            bookMapper.toPaginationDto(books.size.toLong(), page, size)
        } returns genPaginationDto(books.size.toLong(), page, size)

        val actual = service.getBookByCategory("null", page, size)

        // books
        actual.books.size shouldBe expectedPage.content.size
        actual.books shouldBe books.map { it.toSearchResponseDto() }
        actual.books[0].id shouldBe books[0].id
        actual.books[0].name shouldBe books[0].name
        actual.books[0].image shouldBe books[0].image

        // paginationDto
        actual.pagination.currentPage shouldBe page
        actual.pagination.totalPages shouldBe expectedPage.totalPages
        actual.pagination.totalElements shouldBe books.size.toLong()
    }

    @Test
    fun `도서 카테고리별 조회 성공시 BookListResponseDto를 반환한다`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val categoryMajor = "총류"

        val books = mutableListOf(
            genBookSearch(id = 1L,
                name = "book1",
                image = "image/book1.jpg",
                categorySub = categoryMajor,
                categoryMajor = "형이상학",
                author = "김영학",
                expired = false,
                rating = BigDecimal(4.5)
            ),genBookSearch(id = 2L,
                name = "book2",
                image = "image/book2.jpg",
                categorySub = categoryMajor,
                categoryMajor = "도서관학",
                author = "김영학",
                expired = false,
                rating = BigDecimal(4.8)
            ),genBookSearch(id = 3L,
                name = "book3",
                image = "image/book3.jpg",
                categorySub = categoryMajor,
                categoryMajor = "불교",
                author = "김영학",
                expired = false,
                rating = BigDecimal(3.8)
            ),
        )

        val expectedPage = PageImpl(books, pageable, books.size.toLong())

        every { bookSearchRepository.findByExpiredFalseAndCategoryMajor(categoryMajor, pageable) } returns expectedPage

        every {
            bookSearchMapper.toResponseDto(books)
        } returns books.map { it.toSearchResponseDto()}.toMutableList()

        every {
            bookMapper.toPaginationDto(books.size.toLong(), page, size)
        } returns genPaginationDto(books.size.toLong(), page, size)

        val actual = service.getBookByCategory(categoryMajor, page, size)

        // books
        actual.books.size shouldBe expectedPage.content.size
        actual.books shouldBe books.map { it.toSearchResponseDto() }
        actual.books[0].id shouldBe books[0].id
        actual.books[0].name shouldBe books[0].name
        actual.books[0].image shouldBe books[0].image

        // paginationDto
        actual.pagination.currentPage shouldBe page
        actual.pagination.totalPages shouldBe expectedPage.totalPages
        actual.pagination.totalElements shouldBe books.size.toLong()

    }

    @Test
    fun `도서 전체 조회시 데이터가 존재하지 않을 경우 빈 리스트를 반환한다`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val books: MutableList<BookSearch> = mutableListOf()

        val expectedPage = PageImpl(books, pageable, books.size.toLong())
        every { bookSearchRepository.findByExpiredFalse(pageable) } returns expectedPage
        every {
            bookSearchMapper.toResponseDto(books)
        } returns books.map { it.toSearchResponseDto()}.toMutableList()
        every {
            bookMapper.toPaginationDto(books.size.toLong(), page, size)
        } returns genPaginationDto(books.size.toLong(), page, size)

        val actual = service.getBookByCategory("null", page, size)

        actual.books.size shouldBe expectedPage.content.size
        actual.books shouldBe books.map { it.toSearchResponseDto() }
    }

    @Test
    fun `도서 카테고리별 조회시 데이터가 존재하지 않을 경우 빈 리스트를 반환한다`() {
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val categoryMajor = "총류"

        val books: MutableList<BookSearch> = mutableListOf()

        val expectedPage = PageImpl(books, pageable, books.size.toLong())
        every { bookSearchRepository.findByExpiredFalseAndCategoryMajor(categoryMajor,pageable) } returns expectedPage
        every {
            bookSearchMapper.toResponseDto(books)
        } returns books.map { it.toSearchResponseDto()}.toMutableList()
        every {
            bookMapper.toPaginationDto(books.size.toLong(), page, size)
        } returns genPaginationDto(books.size.toLong(), page, size)

        val actual = service.getBookByCategory(categoryMajor, page, size)

        actual.books.size shouldBe expectedPage.content.size
        actual.books shouldBe books.map { it.toSearchResponseDto() }
    }

    @Test
    fun `도서 검색시 입력한 키워드에 해당하는 데이터를 조회한다`() {

        val keyword = "코틀린"
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val book1 = genBookSearch(id = 1L,
            name = "코틀린 인 액션",
            image = "image/코틀린 인 액션.jpg",
            categorySub = "철학",
            categoryMajor = "형이상학",
            author = "코틀린",
            expired = false,
            rating = BigDecimal("4.5")
        )
        val book2 = genBookSearch(id = 2L,
            name = "코틀린의 정석",
            image = "image/book2.jpg",
            categorySub = "총류",
            categoryMajor = "도서관학",
            author = "김영학",
            expired = false,
            rating = BigDecimal("4.8")
        )

        val book3 = genBookSearch(id = 3L,
            name = "book3",
            image = "image/book3.jpg",
            categorySub = "종교",
            categoryMajor = "불교",
            author = "코틀린",
            expired = false,
            rating = BigDecimal("3.8")
        )

        val namePage: Page<BookSearch> = PageImpl(listOf(book1, book2))
        val authorPage: Page<BookSearch> = PageImpl(listOf(book1, book3))

        every { bookSearchRepository.findByExpiredFalseAndNameContaining(keyword, pageable) } returns namePage
        every { bookSearchRepository.findByExpiredFalseAndAuthorContaining(keyword, pageable) } returns authorPage

        val expectedBooks = mutableListOf(namePage, authorPage)
            .flatMap { it.content }
            .distinctBy { it.id }
            .sortedBy { it.id }
            .toMutableList()

        val paginationDto = genPaginationDto(expectedBooks.size.toLong(), page, size)

        every { bookSearchMapper.toResponseDto(any()) } returns expectedBooks.map { it.toSearchResponseDto() }.toMutableList()
        every { bookMapper.toPaginationDto(expectedBooks.size.toLong(), page, size) } returns paginationDto

        val actual = service.searchBooks(keyword, page, size)

        actual.books.size shouldBe expectedBooks.size
        actual.books shouldBe expectedBooks.map { it.toSearchResponseDto() }
        actual.pagination.currentPage shouldBe page
        actual.pagination.totalElements shouldBe expectedBooks.size.toLong()
    }

    @Test
    fun `도서 검색시 입력한 키워드에 해당하는 데이터가 존재하지 않을 경우 빈리스트를 반환한다`() {
        val keyword = "코틀린"
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page-1,size, Sort.by(Sort.Direction.ASC, "id"))

        val namePage: Page<BookSearch> = PageImpl(listOf())
        val authorPage: Page<BookSearch> = PageImpl(listOf())

        every { bookSearchRepository.findByExpiredFalseAndNameContaining(keyword, pageable) } returns namePage
        every { bookSearchRepository.findByExpiredFalseAndAuthorContaining(keyword, pageable) } returns authorPage

        val expectedBooks = mutableListOf<BookSearchResponseDto>()

        every { bookSearchMapper.toResponseDto(any()) } returns expectedBooks
        every { bookMapper.toPaginationDto(expectedBooks.size.toLong(), page, size) } returns genPaginationDto(expectedBooks.size.toLong(), page, size)

        val actual = service.searchBooks(keyword, page, size)

        actual.books.size shouldBe expectedBooks.size
        actual.books shouldBe expectedBooks
        actual.pagination.currentPage shouldBe page
        actual.pagination.totalElements shouldBe expectedBooks.size.toLong()
    }

    @Test
    fun `뷰어 요청시 id에 해당하는 데이터가 존재하면 ViewerResponseDto를 반환한다`() {
        val id = 1L

        val publisherName = "한빛미디어"
        val authorName = "김영학"
        val categoryMajor = "철학"
        val categorySub = "형이상학"

        val publisher = genPublisher(id, publisherName)
        val author = genAuthor(id, authorName)
        val category = genCategory(id, categoryMajor, categorySub)


        val request = genBookReq(
            name = "test",
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
        val epubUrl = "epub/${request.name}.epub"

        val book = genBook(
            id = id,
            req = request,
            pub = publisher,
            author = author,
            category = category,
            imageUrl = epubUrl
        )

        val expectedEpubUrl = "https://nCloudAddress.com/$epubUrl"


        every { bookRepository.findByIdAndExpiredFalse(id) } returns book
        every { nCloudStorageService.generateObjectUrl(epubUrl) } returns expectedEpubUrl

        val actual = service.getViewerBook(id)

        actual.epubUri shouldBe expectedEpubUrl

    }

}