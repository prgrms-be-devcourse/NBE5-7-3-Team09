package ninegle.Readio.library.service

import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.library.domain.Library
import ninegle.Readio.library.dto.library.*
import ninegle.Readio.library.mapper.LibraryMapper
import ninegle.Readio.library.repository.LibraryRepository
import ninegle.Readio.user.repository.UserRepository
import ninegle.Readio.user.service.UserContextService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service

open class LibraryService (
    private val libraryRepository: LibraryRepository,
    private val userContextService: UserContextService,
    private val userRepository: UserRepository,
    private val libraryMapper: LibraryMapper
){

    //라이브러리 생성
    @Transactional
    open fun newLibrary(newLibraryRequestDto: NewLibraryRequestDto): NewLibraryResponseDto {
        //유저 정보를 꺼내고

        val userId = userContextService.currentUserId ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val finduser = userRepository.findById(userId)

        if (finduser.isEmpty) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        val user = finduser.get()

        //들어온 라이브러리 이름 dto에서 꺼내기
        val library: Library = libraryMapper.toNewLibraryEntity(newLibraryRequestDto, user)
        libraryRepository.save(library)
        val responseDto: NewLibraryResponseDto = libraryMapper.fromNewLibraryResponseDto(
            library.id,
            library.libraryName,
            user.id
        )

        return responseDto
    }

    //라이브러리 전체 조회
    @Transactional(readOnly = true)
    open fun getAllLibraries(pageable: Pageable): LibraryListResponseDto {
        val userId = userContextService.currentUserId ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val finduser = userRepository.findById(userId)

        if (finduser.isEmpty) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        val libraries = libraryRepository.findAllByUserId(userId, pageable)
        val libraryListResponseDto: LibraryListResponseDto = libraryMapper.fromLibraryListResponseDto(libraries)
        return libraryListResponseDto
    }

    //라이브러리 삭제
    @Transactional
    open fun deleteLibrary(libraryId: Long) {
        val userId = userContextService.currentUserId ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val finduser = userRepository.findById(userId)

        if (finduser.isEmpty) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND) //404
        }

        val library = libraryRepository.findByIdAndUserId(libraryId, userId)
        libraryRepository.delete(library)
    }

    //라이브러리 이름 수정
    @Transactional
    open fun updateLibrary(
        libraryId: Long,
        updateLibraryRequestDto: UpdateLibraryRequestDto
    ): UpdateLibraryResponseDto {
        //param으로 들어온 id, body로 들어온 변경된 라이브러리 Name
        val userId = userContextService.currentUserId ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
        val finduser = userRepository.findById(userId)

        if (finduser.isEmpty) {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        //바꿀 라이브러리를 가져온다
        val searchlibrary = libraryRepository.findByIdAndUserId(libraryId, userId) ?: throw BusinessException(ErrorCode.LIBRARY_NOT_FOUND)
        //새로 바꿔줄 이름
        val libraryName: String = libraryMapper.toLibraryName(updateLibraryRequestDto)

        //가져온 라이브러리에 이름 변경
        val library = searchlibrary.changeLibraryName(libraryName)
        libraryRepository.save(library)
        val responseDto: UpdateLibraryResponseDto = libraryMapper.fromUpdateLibraryResponseDto(
            library.id,
            library.libraryName
        )

        return responseDto
    }
}
