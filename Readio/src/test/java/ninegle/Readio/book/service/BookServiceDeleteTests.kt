package ninegle.Readio.book.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.adapter.service.NCloudStorageService
import ninegle.Readio.book.domain.BookSearch
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
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.publisher.repository.PublisherRepository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.test.Test

class BookServiceDeleteTests {


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

    val mockEpubFile = genMockMultipartFile(
        name = "EpubFile",
        originalFilename = "test.epub",
        contentType = "application/epub+zip",
        content = "test".toByteArray()
    )
    val mockImageFile = genMockMultipartFile(
        name = "ImageFile",
        originalFilename = "test.jpg",
        contentType = "image/jpeg",
        content = "test".toByteArray()
    )


    @Test
    fun `도서 삭제 성공시 Soft_Delete를 수행한다`() {

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

        val deletedBookSearch = BookSearch(
            id = targetBookSearch.id,
            name = targetBookSearch.name,
            image = targetBookSearch.image,
            categorySub = targetBookSearch.categorySub,
            categoryMajor = targetBookSearch.categoryMajor,
            author = targetBookSearch.author,
            expired = true,
            rating = targetBookSearch.rating,
        )

        every { bookRepository.findByIdAndExpiredFalse(id) } returns targetBook
        every { bookRepository.delete(targetBook) } returns Unit
        every { bookSearchRepository.findById(id) } returns Optional.of(targetBookSearch)
        every { bookSearchRepository.save(targetBookSearch) } returns deletedBookSearch

        service.deleteBook(id)

        verify(exactly = 1) {
            bookRepository.findByIdAndExpiredFalse(id)
            bookSearchRepository.findById(id)
            bookSearchRepository.save(deletedBookSearch)
        }

    }


}