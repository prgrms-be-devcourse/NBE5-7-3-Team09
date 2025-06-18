package ninegle.Readio.library.dto.library

import java.time.LocalDateTime


data class AllLibraryDto(
    val id: Long?,
    val libraryName: String,
    val createAt: LocalDateTime,
    val updateAt: LocalDateTime?
)
