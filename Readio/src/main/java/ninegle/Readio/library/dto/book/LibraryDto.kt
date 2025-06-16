package ninegle.Readio.library.dto.book

import java.time.LocalDateTime


data class LibraryDto(
    val libraryId: Long?,
    val libraryName: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime?
)
