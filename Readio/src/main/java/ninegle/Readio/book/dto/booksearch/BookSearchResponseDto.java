package ninegle.Readio.book.dto.booksearch;

import java.math.BigDecimal;

import lombok.Builder;


@Builder
public record BookSearchResponseDto(long id, String name, String image, String categoryMajor, String categorySub,
									String authorName, BigDecimal rating) {

}
