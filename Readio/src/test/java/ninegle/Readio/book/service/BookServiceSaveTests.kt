package ninegle.Readio.book.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.adapter.service.NCloudStorageService
import ninegle.Readio.book.mapper.BookMapper
import ninegle.Readio.book.mapper.BookSearchMapper
import ninegle.Readio.book.repository.AuthorRepository
import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.book.repository.BookSearchRepository
import ninegle.Readio.book.util.genAuthor
import ninegle.Readio.book.util.genBook
import ninegle.Readio.book.util.genBookReq
import ninegle.Readio.book.util.genBookSearch
import ninegle.Readio.book.util.genMockMultipartFile
import ninegle.Readio.category.repository.CategoryRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.publisher.repository.PublisherRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class BookServiceSaveTests {

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
    }

    @Test
    fun `도서 저장 성공 테스트`() {

        // given
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

        val expectedCategory =   genCategory(110, "철학", request.categorySub)
        val expectedPublisher = genPublisher(1L, request.publisherName)
        val expectedAuthor = genAuthor(1L, request.authorName)


        val expectedEpubFileKey = "epub/${request.name}.epub"
        val expectedImageFileKey = "image/${request.name}.jpg"

        val expectedBook = genBook(null,request, expectedPublisher, expectedAuthor, expectedCategory, expectedImageFileKey)
        val expectedBookSearch = genBookSearch(null, "test", expectedImageFileKey, expectedCategory.sub, expectedCategory.major, request.authorName, expired = false, rating = BigDecimal.ZERO )

        // when
        every { bookRepository.existsByIsbn(request.isbn) } returns false
        every { bookRepository.existsByEcn(request.ecn) } returns false

        every { nCloudStorageService.fileExists(expectedEpubFileKey) } returns false
        every { nCloudStorageService.uploadFile(expectedEpubFileKey, request.epubFile) } just Runs

        every { nCloudStorageService.fileExists(expectedImageFileKey) } returns false
        every { nCloudStorageService.uploadFile(expectedImageFileKey, request.image) } just Runs

        every { categoryRepository.findBySub(request.categorySub) } returns expectedCategory
        every { publisherRepository.findByName(request.publisherName) } returns expectedPublisher
        every { authorRepository.findByName(request.authorName) } returns expectedAuthor


        every { bookRepository.save(expectedBook) } returns expectedBook
        every { bookSearchRepository.save(expectedBookSearch) } returns expectedBookSearch

        service.save(request)

        // then
        verify(exactly = 1) {
            bookRepository.existsByIsbn(request.isbn)
            bookRepository.existsByEcn(request.ecn)

            nCloudStorageService.fileExists(expectedEpubFileKey)
            nCloudStorageService.uploadFile(expectedEpubFileKey, request.epubFile)
            nCloudStorageService.fileExists(expectedImageFileKey)
            nCloudStorageService.uploadFile(expectedImageFileKey, request.image)

            categoryRepository.findBySub(request.categorySub)
            publisherRepository.findByName(request.publisherName)
            authorRepository.findByName(request.authorName)

            bookRepository.save(expectedBook)
            bookSearchRepository.save(expectedBookSearch)
        }

    }

    @Test
    fun `도서 데이터 저장 시 중복되는 ISBN이 입력될 경우 실패하는 테스트`() {

        // given
        val mockEpubFile =
            genMockMultipartFile("EpubFile", "test.epub", "application/epub+zip", "test".toByteArray())

        val mockImageFile = genMockMultipartFile("ImageFile", "test.jpg", "image/jpeg", "test".toByteArray())

        val conflictIsbn = UUID.randomUUID().toString()

        val request = genBookReq(
            name = "test",
            description = "책 설명",
            image = mockImageFile,
            isbn = conflictIsbn,
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김작가"
        )

        // when
        every { bookRepository.existsByIsbn(request.isbn) } returns true

        val actual = assertThrows<BusinessException> { service.save(request) }

        assertEquals(HttpStatus.CONFLICT,actual.errorCode.status)
        assertEquals("이미 등록된 ISBN입니다.",actual.message)
        assertEquals("DUPLICATE_ISBN",actual.errorCode.name)

        verify(exactly = 1) { bookRepository.existsByIsbn(request.isbn) }

    }

    @Test
    fun `도서 데이터 저장시 중복되는 ECN이 입력될 경우 실패하는 테스트`() {

        // given
        val conflictEcn = UUID.randomUUID().toString()

        val request = genBookReq(
            name = "test",
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = conflictEcn,
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = "형이상학",
            publisherName = "한빛미디어",
            authorName = "김작가"
        )

        every { bookRepository.existsByIsbn(request.isbn) } returns false
        every { bookRepository.existsByEcn(request.ecn) } returns true

        val actual = assertThrows<BusinessException> { service.save(request) }

        assertEquals(HttpStatus.CONFLICT, actual.errorCode.status)
        assertEquals("이미 등록된 ECN입니다.", actual.message)
        assertEquals("DUPLICATE_ECN", actual.errorCode.name)

        verify(exactly = 1) { bookRepository.existsByEcn(request.ecn) }

    }

    @Test
    fun `도서 데이터 저장시 ncp에 epub 파일이 이미 저장된 이름이라면 실패하는 테스트`() {

        // given
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

        val epubFileKey = "epub/${request.name}.epub"

        every { bookRepository.existsByIsbn(request.isbn) } returns false
        every { bookRepository.existsByEcn(request.ecn) } returns false

        every { nCloudStorageService.fileExists(epubFileKey) } returns true

        val actual = assertThrows<BusinessException> { service.save(request) }

        assertEquals("이미 존재하는 이름입니다.",actual.message)
        assertEquals("DUPLICATE_NAME",actual.errorCode.name)
        assertEquals(HttpStatus.CONFLICT,actual.errorCode.status)
    }

    @Test
    fun `도서 데이터 저장시 ncp에 이미지 파일이 이미 저장된 이름이라면 실패하는 테스트`() {

        // given
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

        val epubFileKey = "epub/${request.name}.epub"
        val imageFileKey = "image/${request.name}.jpg"

        every { bookRepository.existsByIsbn(request.isbn) } returns false
        every { bookRepository.existsByEcn(request.ecn) } returns false

        every { nCloudStorageService.fileExists(epubFileKey) } returns false
        every { nCloudStorageService.uploadFile(epubFileKey, request.epubFile) } just Runs

        every { nCloudStorageService.fileExists(imageFileKey) } returns true


        val actual = assertThrows<BusinessException> { service.save(request) }

        assertEquals("이미 존재하는 이름입니다.",actual.message)
        assertEquals("DUPLICATE_NAME",actual.errorCode.name)
        assertEquals(HttpStatus.CONFLICT,actual.errorCode.status)
    }

    @Test
    fun `도서 데이터 저장시 존재하지 않는 카테고리라멵 실패하는 테스트`() {

        // given
        val request = genBookReq(
            name = "test",
            description = "책 설명",
            image = mockImageFile,
            isbn = UUID.randomUUID().toString(),
            ecn = UUID.randomUUID().toString(),
            pubDate = LocalDate.of(2022, 1, 1),
            epubFile = mockEpubFile,
            categorySub = "없는 카테고리",
            publisherName = "한빛미디어",
            authorName = "김작가"
        )

        val epubFileKey = "epub/${request.name}.epub"
        val imageFileKey = "image/${request.name}.jpg"

        every { bookRepository.existsByIsbn(request.isbn) } returns false
        every { bookRepository.existsByEcn(request.ecn) } returns false

        every { nCloudStorageService.fileExists(epubFileKey) } returns false
        every { nCloudStorageService.uploadFile(epubFileKey, request.epubFile) } just Runs

        every { nCloudStorageService.fileExists(imageFileKey) } returns false
        every { nCloudStorageService.uploadFile(imageFileKey, request.image) } just Runs

        every { categoryRepository.findBySub(request.categorySub) } returns null



        val actual = assertThrows<BusinessException> { service.save(request) }

        assertEquals("해당 카테고리를 찾을 수 없습니다.",actual.message)
        assertEquals("CATEGORY_NOT_FOUND",actual.errorCode.name)
        assertEquals(HttpStatus.NOT_FOUND,actual.errorCode.status)
    }

}