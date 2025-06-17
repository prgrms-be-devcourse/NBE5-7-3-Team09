package ninegle.Readio.library.dto.book

import java.math.BigDecimal
import java.time.LocalDate


data class AllLibraryBooksDto(
    val bookId: Long?,
    val bookName: String?,
    val bookImage: String,
    val bookIsbn: String?,
    val bookEcn: String?,
    val bookPubDate: LocalDate,
    val bookUpdateAt: LocalDate?,
    val rating: BigDecimal?
)
