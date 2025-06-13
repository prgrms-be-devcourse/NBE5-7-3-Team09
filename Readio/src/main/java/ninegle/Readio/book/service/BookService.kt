package ninegle.Readio.book.service

import lombok.extern.slf4j.Slf4j
import ninegle.Readio.adapter.service.NCloudStorageService
import ninegle.Readio.book.domain.Author
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.booksearch.BookListResponseDto
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
import ninegle.Readio.book.dto.viewer.ViewerResponseDto
import ninegle.Readio.book.mapper.BookMapper
import ninegle.Readio.book.mapper.BookSearchMapper
import ninegle.Readio.book.mapper.toBookListResponseDto
import ninegle.Readio.book.mapper.toEntity
import ninegle.Readio.book.repository.AuthorRepository
import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.book.repository.BookSearchRepository
import ninegle.Readio.category.domain.Category
import ninegle.Readio.category.repository.CategoryRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.publisher.repository.PublisherRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.time.LocalDateTime
import java.util.function.Supplier

/**
 * Readio - BookService
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Slf4j
@Service
@EnableScheduling
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val categoryRepository: CategoryRepository,
    private val publisherRepository: PublisherRepository,
    private val bookSearchRepository: BookSearchRepository,
    private val nCloudStorageService: NCloudStorageService,
    private val bookSearchMapper: BookSearchMapper,
    private val bookMapper: BookMapper
) {

    @Transactional(readOnly = true)
    fun searchBooks(keyword: String, page: Int, size: Int): BookListResponseDto {
        val findBooks = bookSearchMapper.toResponseDto(getBookSearchList(keyword, page, size))

        val totalElements = findBooks.size.toLong()
        val paginationDto = bookMapper.toPaginationDto(totalElements, page, size)

        return findBooks.toBookListResponseDto(paginationDto)
    }

    private fun getBookSearchList(keyword: String, page: Int, size: Int): MutableList<BookSearch> {
        val result: MutableSet<BookSearch> = LinkedHashSet()
        val pageable: Pageable = PageRequest.of(page - 1, size)

        result.addAll(bookSearchRepository.findByExpiredFalseAndAuthorContaining(keyword, pageable).getContent())
        result.addAll(bookSearchRepository.findByExpiredFalseAndNameContaining(keyword, pageable).getContent())

        return result.toMutableList()
    }

    @Transactional
    @Throws(IOException::class)
    fun save(request: BookRequestDto) {
        validateSavedBook(request)

        // 1. S3에 업로드할 파일명 생성 ( 책 제목 기반 )
        val fileKey = "epub/${request.name}.epub"
        val imageKey = "image/${request.name}.jpg"

        // 2. S3에 해당 파일 존재 여부 확인
        if (!nCloudStorageService.fileExists(fileKey)) {
            nCloudStorageService.uploadFile(fileKey, request.epubFile)
        }
        if (!nCloudStorageService.fileExists(imageKey)) {
            nCloudStorageService.uploadFile(imageKey, request.image)
        }

        // 3. 연관 엔티티 조회
        val category = getCategory(request.categorySub)
        val author = getAuthor(request.authorName)
        val publisher = getPublisher(request.publisherName)

        // 4. Book 저장
        val savedBook: Book = bookRepository.save(request.toEntity(publisher, author, category, imageKey))

        // 5. ElasticSearch Repository에 저장
        bookSearchRepository.save(savedBook.toEntity())
    }

    private fun validateSavedBook(request: BookRequestDto) {
        if (existsByIsbn(request.isbn)) {
            throw BusinessException(ErrorCode.DUPLICATE_ISBN)
        }
        if (existsByEcn(request.ecn)) {
            throw BusinessException(ErrorCode.DUPLICATE_ECN)
        }
    }

    private fun existsByIsbn(isbn: String): Boolean {
        return bookRepository.existsByIsbn(isbn)
    }

    private fun existsByEcn(ecn: String): Boolean {
        return bookRepository.existsByEcn(ecn)
    }

    // 카테고리가 존재하면 Get, 존재하지 않다면 Throw 발생
    private fun getCategory(categorySub: String): Category {
        return categoryRepository.findBySub(categorySub) ?: throw BusinessException(ErrorCode.CATEGORY_NOT_FOUND)
    }

    // 작가가 존재하면 해당 작가를 Get, 존재하지 않다면 새로운 작가 생성 후 Get
    private fun getAuthor(authorName: String): Author {
        return authorRepository.findByName(authorName) ?: authorRepository.save(Author(name=authorName))
    }

    // 출판사가 존재하면 해당 출판사 Get, 존재하지 않다면 새로운 출판사 생성 후 Get
    private fun getPublisher(publisherName: String): Publisher {
        return publisherRepository.findByName(publisherName) ?: publisherRepository.save(Publisher(name = publisherName))
    }

    @Transactional(readOnly = true)
    fun getBookDetail(id: Long): BookResponseDto {
        val findBook = getBookByIdAndExpired(id)
        if (findBook.expired) {
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }

        return bookMapper.toDto(findBook)
    }

    private fun validateUpdatedBook(request: BookRequestDto, bookId: Long) {
        if (bookRepository.existsByIsbnAndIdNot(request.isbn, bookId)) {
            throw BusinessException(ErrorCode.DUPLICATE_ISBN)
        }
        if (request.ecn != null && bookRepository.existsByEcnAndIdNot(request.ecn, bookId)) {
            throw BusinessException(ErrorCode.DUPLICATE_ECN)
        }
    }

    @Transactional
    fun updateBook(id: Long, request: BookRequestDto): BookResponseDto {
        val targetBook = getBookById(id)
        if (targetBook.isbn != request.isbn || targetBook.ecn != request.ecn) {
            validateUpdatedBook(request, id)
        }

        val targetBookSearch = bookSearchRepository.findById(id).orElseThrow { BusinessException(ErrorCode.BOOK_NOT_FOUND) }


        val beforeName = targetBook.name
        val afterName = request.name

        // 이름이 바뀌었으면 epub, image 파일 rename
        if (beforeName != afterName) {
            nCloudStorageService.renameFileOnCloud(beforeName, afterName, "epub", ".epub") // epub 파일명 변경
            nCloudStorageService.renameFileOnCloud(beforeName, afterName, "image", ".jpg")
        }
        val updatedImageUrl = "image/${afterName}.jpg"

        val category = getCategory(request.categorySub)
        val author = getAuthor(request.authorName)
        val publisher = getPublisher(request.publisherName)

        val updatedBook = targetBook.update(request, category, author, publisher, updatedImageUrl)
        val updatedBookSearch = targetBookSearch.update(request, category, author, updatedImageUrl)

        bookRepository.save<Book>(updatedBook)
        bookSearchRepository.save<BookSearch>(updatedBookSearch)

        return bookMapper.toDto(updatedBook)
    }

    // 만료 되지 않은 책 get
    private fun getBookByIdAndExpired(id: Long): Book {
        return bookRepository.findByIdAndExpiredFalse(id) ?: throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
    }

    fun getBookById(id: Long): Book {
        return bookRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.BOOK_NOT_FOUND) }
    }

    @Transactional
    fun deleteBook(id: Long) {
        val findBook = getBookByIdAndExpired(id)
        bookRepository.delete(findBook)

        val findBookSearch = bookSearchRepository.findById(id)
            .orElseThrow { BusinessException(ErrorCode.BOOK_NOT_FOUND) }

        if (!findBookSearch.expired) {
            findBookSearch.softDelete()
            bookSearchRepository.save(findBookSearch)
        }
    }

    // 0시 0분 기준 만료된지 7일 지난 도서 삭제
    @Scheduled(cron = "0 0 0 * * ?")
    fun deleteExpiredBooks() {
        val threshold = LocalDateTime.now().minusDays(7)

        // 삭제 id 목록 조회
        val expiredBooks: MutableList<Book> = bookRepository.findByExpiredTrueAndExpiredAtBefore(threshold)
        if (expiredBooks.isEmpty()) {
            return
        }
        val ids = expiredBooks.stream().map<Long?>(Book::id).toList()

        // JPA 물리 삭제
        val deleteCount = bookRepository.deleteExpiredBefore(threshold)

        // ES 물리 삭제
        bookSearchRepository.deleteAllById(ids)

        // ncp 파일 삭제
        expiredBooks.forEach { book ->
            nCloudStorageService.deleteFileOnCloud(book.name, "epub", ".epub")
            nCloudStorageService.deleteFileOnCloud(book.name, "image", ".jpg")
        }
    }

    @Transactional(readOnly = true)
    fun getBookByCategory(categoryMajor: String, page: Int, size: Int): BookListResponseDto {
        val pageable: Pageable = PageRequest.of(page - 1, size)
        val findBooks: Page<BookSearch> = if (categoryMajor == "null")
            bookSearchRepository.findByExpiredFalse(pageable)
        else
            bookSearchRepository.findByExpiredFalseAndCategoryMajor(categoryMajor, pageable)

        // 총 책의 개수
        val totalElements = findBooks.totalElements

        val books = findBooks.getContent()
        val responseDtos: MutableList<BookSearchResponseDto> = bookSearchMapper.toResponseDto(books)
        val paginationDto = bookMapper.toPaginationDto(totalElements, page, size)

        return responseDtos.toBookListResponseDto(paginationDto)
    }

    @Transactional(readOnly = true)
    fun getViewerBook(id: Long): ViewerResponseDto? {
        val findBook = getBookByIdAndExpired(id)

        val epubUri = nCloudStorageService.generateObjectUrl("epub/${findBook.name}.epub")

        return ViewerResponseDto(epubUri)
    }
}