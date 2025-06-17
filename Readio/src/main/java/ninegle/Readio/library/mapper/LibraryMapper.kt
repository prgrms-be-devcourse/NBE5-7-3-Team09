package ninegle.Readio.library.mapper

import ninegle.Readio.library.domain.Library
import ninegle.Readio.library.dto.library.*
import ninegle.Readio.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
class LibraryMapper {
    //DTO에서 라이브러리로

	fun toNewLibraryEntity(newLibraryRequestDto: NewLibraryRequestDto, user: User): Library {
        return Library(newLibraryRequestDto.libraryName, user)
    }

    //라이브러리에서 DTO로
	fun fromNewLibraryResponseDto(libraryid: Long?, libraryname: String, userid: Long?): NewLibraryResponseDto {
        val newLibraryResponseDto = NewLibraryResponseDto(
            libraryId = libraryid,
            libraryName = libraryname,
            userId = userid)
        return newLibraryResponseDto
    }

    //전체 라이브러리 조회
    fun fromLibraryListResponseDto(librarypage: Page<Library>): LibraryListResponseDto {
        val libraryList: List<AllLibraryDto> = librarypage.content
            .map { library: Library ->
                AllLibraryDto(
                    id = library.id,
                    libraryName = library.libraryName,
                    createAt = library.createdAt,
                    updateAt = library.updatedAt)
            }
        return LibraryListResponseDto(
            allLibraries = libraryList,
            totalCount = librarypage.totalElements,
            page = librarypage.number+1,
            size = librarypage.size )

    }

    //라이브러리 이름 수정
	fun toLibraryName(updateLibraryRequestDto: UpdateLibraryRequestDto): String {
        return updateLibraryRequestDto.libraryName
    }

    //라이브러리 이름 수정 후 Response
	fun fromUpdateLibraryResponseDto(libraryid: Long?, libraryname: String): UpdateLibraryResponseDto {
        return UpdateLibraryResponseDto(
            id = libraryid,
            libraryName = libraryname)

    }
}
