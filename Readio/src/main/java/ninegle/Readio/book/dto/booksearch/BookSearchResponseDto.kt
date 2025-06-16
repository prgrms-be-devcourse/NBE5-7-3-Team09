package ninegle.Readio.book.dto.booksearch

import java.math.BigDecimal

data class BookSearchResponseDto(
    val id: Long?,
    val name: String,
    val image: String,
    val categoryMajor: String,
    val categorySub: String,
    val authorName: String,
    val rating: BigDecimal
)
