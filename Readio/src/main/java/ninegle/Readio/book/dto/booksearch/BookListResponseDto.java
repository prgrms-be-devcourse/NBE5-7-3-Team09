package ninegle.Readio.book.dto.booksearch;

import java.util.List;

import lombok.Builder;
import ninegle.Readio.book.dto.PaginationDto;

@Builder
public record BookListResponseDto(List<BookSearchResponseDto> books, PaginationDto pagination) {
}
