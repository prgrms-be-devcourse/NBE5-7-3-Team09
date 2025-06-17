package ninegle.Readio.library.service

import ninegle.Readio.book.repository.BookRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.library.domain.Library
import ninegle.Readio.library.domain.LibraryBook
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto
import ninegle.Readio.library.mapper.LibraryBookMapper
import ninegle.Readio.library.repository.LibraryBookRepository
import ninegle.Readio.library.repository.LibraryRepository
import ninegle.Readio.user.service.UserContextService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service

open class LibraryBookService (
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository,
    private val libraryBookRepository: LibraryBookRepository,
    private val userContextService: UserContextService,
    private val libraryBookMapper: LibraryBookMapper)
{

    //라이브러리에 책 저장
    @Transactional
    open fun newLibraryBook(libraryId: Long, bookRequestDto: NewLibraryBookRequestDto) {
        //라이브러리 가져오기
        val library = getLibrary(libraryId)

        //라이브러리에 추가할 책
        val newLibraryBookId = libraryBookMapper.toNewLibraryBook(bookRequestDto)
        val findBook = bookRepository.findById(newLibraryBookId)
        if (findBook.isEmpty) {
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND) //404
        }
        val book = findBook.get()

        //단지 중복 검사로 기존 라이브러리에 책이 있는지만 확인
        val existing = libraryBookRepository.duplicateTest(libraryId, book.id)
        // 만약 이미 존재해서 null이 아니면 예외를 던짐
        if (existing != null) {
            throw BusinessException(ErrorCode.BOOK_ALREADY_EXISTS) }

        //존재하지 않으면 책을 해당 라이브러리에 저장
        val libraryBook = LibraryBook(
            book = book,
            library = library)
        libraryBookRepository.save(libraryBook)
    }


    //라이브러리에 책들 불러오기
    @Transactional(readOnly = true)
    open fun getAllLibraryBooks(
        libraryId: Long,
        pageable: Pageable): LibraryBookListResponseDto {

        val library = getLibrary(libraryId)

        //조회한 책들
        val books = libraryBookRepository.findBookByLibraryId(libraryId, pageable)
        val libraryBookListResponseDto = libraryBookMapper.libraryBookListResponseDto(library, books)
        return libraryBookListResponseDto
    }


    //라이브러리에 책 삭제
    @Transactional
    open fun deleteLibraryBook(libraryId: Long, BookId: Long) {
        val library = getLibrary(libraryId)
        val libraryBook = libraryBookRepository.findLibraryBook(libraryId, BookId) ?: throw BusinessException(ErrorCode.BOOK_NOT_FOUND) //404
        libraryBookRepository.delete(libraryBook)
    }

    //공용 메서드
    fun getLibrary(libraryId: Long): Library {
        val libraryOptional = libraryRepository.findById(libraryId)
        if (libraryOptional.isEmpty) {
            throw BusinessException(ErrorCode.LIBRARY_NOT_FOUND)
        }
        val library = libraryOptional.get()
        val currentUserId = userContextService.currentUserId
        val user = library.user
        if (user == null || user.id != currentUserId) {
            throw BusinessException(ErrorCode.FORBIDDEN_ACCESS)
        }
        return libraryOptional.get()
    }
}
