package ninegle.Readio.book.dto.preferencedto

import lombok.Builder
import ninegle.Readio.book.dto.PaginationDto

data class PreferenceListResponseDto(
    val preferences: List<PreferenceResponseDto>?,
    val pagination: PaginationDto
)
