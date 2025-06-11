package ninegle.Readio.library.dto.book;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

@Builder
public record AllLibraryBooksDto(Long bookId, String bookName, String bookImage, String bookIsbn, String bookEcn,
								 LocalDate bookPubDate, LocalDate bookUpdateAt, BigDecimal rating) {
}
