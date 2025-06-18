package ninegle.Readio.book.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.domain.Preference
import ninegle.Readio.book.dto.BookIdRequestDto
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto
import ninegle.Readio.book.mapper.PreferenceMapper
import ninegle.Readio.book.repository.PreferencesRepository
import ninegle.Readio.book.util.*
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.util.genCategory
import ninegle.Readio.global.util.genPublisher
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.service.UserService
import ninegle.Readio.user.util.UserUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockMultipartFile
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class PreferenceServiceTests {
    val preferencesRepository=mockk<PreferencesRepository>()
    val preferenceMapper = mockk<PreferenceMapper>()
    val bookService = mockk<BookService>()
    val userService = mockk<UserService>()

    val preferenceService = PreferenceService(
        preferencesRepository = preferencesRepository,
        preferenceMapper = preferenceMapper,
        bookService = bookService,
        userService = userService
    )

    lateinit var mockEpubFile: MockMultipartFile
    lateinit var mockImageFile: MockMultipartFile
    lateinit var book: Book
    lateinit var user: User
    lateinit var bookSearch : BookSearch
    val preference = mockk<Preference>()

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
    fun `관심도서 불러오기`(){
        every { preferencesRepository.findPreferenceByBookAndUser(book,user) } returns preference

        preferenceService.getPreferenceByBookAndUser(book,user)
    }
    @Test
    fun `관심도서 없을시 오류`(){

        every { preferencesRepository.findPreferenceByBookAndUser(book,user) } returns null

        assertThrows<BusinessException> {
            preferenceService.getPreferenceByBookAndUser(book,user)
        }
    }
    @Test fun `성공 저장`(){
        val dto = BookIdRequestDto(book.id!!)
        val preferenceEntity = mockk<Preference>()
        val preferenceDto = mockk<PreferenceResponseDto>()

        every { bookService.getBookById(book.id!!) } returns book
        every { userService.getById(user.id!!) } returns user
        every { preferencesRepository.findPreferenceByBookAndUser(book, user) } returns null
        every { preferenceMapper.toEntity(user, book) } returns preferenceEntity
        every { preferencesRepository.save(preferenceEntity) } returns preferenceEntity
        every { preferenceMapper.toPreferenceDto(preferenceEntity) } returns preferenceDto

        val result = preferenceService.save(user.id!!, dto)

        assert(result === preferenceDto)
        verify { preferencesRepository.save(preferenceEntity) }
    }
    @Test fun `이미 관심목록에 존재할시 오류`(){
        val dto = BookIdRequestDto(book.id!!)

        every { bookService.getBookById(book.id!!) } returns book
        every { userService.getById(user.id!!) } returns user
        every { preferencesRepository.findPreferenceByBookAndUser(book, user) } returns preference

        assertThrows<BusinessException> {
            preferenceService.save(user.id!!, dto)
        }
    }
    @Test fun `관심목록 리스트 호출 성공`(){
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page - 1, size)
        val preferenceList = listOf(preference)

        val preferenceDtoList = listOf(
            PreferenceResponseDto(
                id = 1L,
                name = book.name,
                image = book.image,
                rating = book.rating
            )
        )

        val paginationDto = PaginationDto(
            totalElements = 1L,
            totalPages = 1,
            currentPage = page,
            size = size
        )

        val expectedResponse = PreferenceListResponseDto(
            preferences = preferenceDtoList,
            pagination = paginationDto
        )

        val preferencePage = mockk<org.springframework.data.domain.Page<Preference>>()
        every { preferencePage.content } returns preferenceList

        every { userService.getById(user.id!!) } returns user
        every { preferencesRepository.countByUser(user) } returns 1L
        every { preferencesRepository.findPreferencesByUser(user, pageable) } returns preferencePage
        every { preferenceMapper.toPreferenceDto(preferenceList) } returns preferenceDtoList
        every { preferenceMapper.toPaginationDto(1L, page, size) } returns paginationDto
        every { preferenceMapper.toPreferenceListDto(preferenceDtoList, paginationDto) } returns expectedResponse

        val result = preferenceService.getPreferenceList(user.id!!, page, size)

        assert(result == expectedResponse)
    }
    @Test fun `관심목록 리스트에 저장된 데이터 없을시 오류`(){
        val page = 1
        val size = 10
        val pageable = PageRequest.of(page - 1, size)

        every { userService.getById(user.id!!) } returns user
        every { preferencesRepository.countByUser(user) } returns 0L
        every { preferencesRepository.findPreferencesByUser(user,pageable) } returns null

        assertThrows<BusinessException> {
            preferenceService.getPreferenceList(user.id!!, page, size)
        }
    }
}