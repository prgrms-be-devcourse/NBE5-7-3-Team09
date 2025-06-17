package ninegle.Readio.library.dto.library


data class LibraryListResponseDto(
    val allLibraries: List<AllLibraryDto>,
    val totalCount: Long,
    val page: Int,
    val size: Int
)
