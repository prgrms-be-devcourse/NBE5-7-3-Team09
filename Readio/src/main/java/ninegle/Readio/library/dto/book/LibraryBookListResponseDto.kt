package ninegle.Readio.library.dto.book


data class LibraryBookListResponseDto(
    val libraryDto: LibraryDto,
    val allLibraryBooks: List<AllLibraryBooksDto>,
    val totalCount: Long,
    val page: Long,
    val size: Long
)
