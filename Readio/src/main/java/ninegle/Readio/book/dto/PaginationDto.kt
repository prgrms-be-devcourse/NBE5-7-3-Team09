package ninegle.Readio.book.dto

data class PaginationDto(
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val size: Int
)