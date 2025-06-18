package ninegle.Readio.book.mapper

import ninegle.Readio.adapter.util.NCloudStorageUtils
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.Preference
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto
import ninegle.Readio.user.domain.User
import org.springframework.stereotype.Component

@Component
class PreferenceMapper {

    fun toEntity(user: User, book: Book): Preference {
        return Preference(
            user = user,
            book= book
        )
    }

    fun toPreferenceDto(preference: Preference): PreferenceResponseDto {
        return PreferenceResponseDto(
            id = preference.book.id!!,
            name = preference.book.name,
            image = NCloudStorageUtils.toImageUrl(preference.book.image),
            rating = preference.book.rating
        )
    }

    fun toPreferenceDto(preferences: List<Preference>): List<PreferenceResponseDto> {
        val preferenceResponseDtos: MutableList<PreferenceResponseDto> = ArrayList()
        for (preference in preferences) {
            preferenceResponseDtos.add(toPreferenceDto(preference))
        }
        return preferenceResponseDtos
    }

    fun toPreferenceListDto(
        preferenceResponseDtos: List<PreferenceResponseDto>?,
        paginationDto: PaginationDto
    ): PreferenceListResponseDto {
        return PreferenceListResponseDto(
            preferences = preferenceResponseDtos,
            pagination = paginationDto)
    }

    fun toPaginationDto(count: Long, page: Int, size: Int): PaginationDto {
        return PaginationDto(
            count,
            (count.toInt() / size) + 1,
            page,
            size
        )
    }
}
