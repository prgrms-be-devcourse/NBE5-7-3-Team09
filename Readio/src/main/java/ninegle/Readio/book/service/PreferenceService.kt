package ninegle.Readio.book.service

import jakarta.transaction.Transactional
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.Preference
import ninegle.Readio.book.dto.BookIdRequestDto
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto
import ninegle.Readio.book.mapper.PreferenceMapper
import ninegle.Readio.book.repository.PreferencesRepository
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.user.domain.User
import ninegle.Readio.user.service.UserContextService
import ninegle.Readio.user.service.UserService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Slf4j
@Service
class PreferenceService(
    val preferencesRepository: PreferencesRepository,
    val preferenceMapper: PreferenceMapper,
    val bookService: BookService,
    val userService: UserService
) {

    fun getPreferenceByBookAndUser(book: Book, user: User): Preference {
        return preferencesRepository.findPreferenceByBookAndUser(book, user)
            ?: throw BusinessException(ErrorCode.PREFERENCE_NOT_FOUND)

    }


    @Transactional
    fun save(userId: Long, dto: BookIdRequestDto): PreferenceResponseDto {
        val book = bookService.getBookById(dto.id!!)
        val user = userService.getById(userId)

        preferencesRepository.findPreferenceByBookAndUser(book, user)?.let{ preference ->
            throw BusinessException(ErrorCode.BOOK_ALREADY_IN_PREFERENCE)
        }

        val preference = preferenceMapper.toEntity(user, book)
        preferencesRepository.save(preference)
        return preferenceMapper.toPreferenceDto(preference)
    }

    @Transactional
    fun delete(userId: Long, bookId: Long): ResponseEntity<BaseResponse<Void>> {
        val book = bookService.getBookById(bookId)
        val user = userService.getById(userId)
        val preference = getPreferenceByBookAndUser(book, user)

        preferencesRepository.delete(preference)
        return BaseResponse.ok("삭제가 성공적으로 수행되었습니다.", null, HttpStatus.OK)
    }

    fun getPreferenceList(userId: Long, page: Int, size: Int): PreferenceListResponseDto {
        val user = userService.getById(userId)
        val pageable: Pageable = PageRequest.of(page - 1, size)
        val count = preferencesRepository.countByUser(user)

        val preferences = preferencesRepository.findPreferencesByUser(user,pageable
        ) ?: throw BusinessException(ErrorCode.PREFERENCE_NOT_FOUND)

        val validPreferences = preferences.content
        val preferenceList = preferenceMapper.toPreferenceDto(validPreferences)

        val paginationDto = preferenceMapper.toPaginationDto(count, page, size)

        return preferenceMapper.toPreferenceListDto(preferenceList, paginationDto)
    }
}
