package ninegle.Readio.book.dto.booksearch

import ninegle.Readio.book.dto.PaginationDto

data class BookListResponseDto(
    val books: MutableList<BookSearchResponseDto>,
    val pagination: PaginationDto
)
