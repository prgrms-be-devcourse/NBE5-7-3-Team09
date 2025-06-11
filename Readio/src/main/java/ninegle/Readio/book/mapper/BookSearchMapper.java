package ninegle.Readio.book.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.adapter.config.NCloudStorageConfig;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.BookSearch;
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto;

@Component
@RequiredArgsConstructor
public class BookSearchMapper {

	private final NCloudStorageConfig nCloudStorageConfig;

	public BookSearch toEntity(Book book) {
		return BookSearch.builder()
			.id(book.getId())
			.name(book.getName())
			.image(book.getImage())
			.expired(false)
			.categoryMajor(book.getCategory().getMajor())
			.categorySub(book.getCategory().getSub())
			.author(book.getAuthor().getName())
			.rating(BigDecimal.ZERO)
			.build();
	}

	public BookSearchResponseDto toDto(BookSearch bookSearch) {
		return BookSearchResponseDto.builder()
			.id(bookSearch.getId())
			.name(bookSearch.getName())
			.image(nCloudStorageConfig.toImageUrl(bookSearch.getImage()))
			.categoryMajor(bookSearch.getCategoryMajor())
			.categorySub(bookSearch.getCategorySub())
			.authorName(bookSearch.getAuthor())
			.rating(bookSearch.getRating())
			.build();
	}

	public List<BookSearchResponseDto> toResponseDto(List<BookSearch> bookList) {
		List<BookSearchResponseDto> bookSearchResponseDtos = new java.util.ArrayList<>();
		for (BookSearch bookSearch : bookList) {
			bookSearchResponseDtos.add(toDto(bookSearch));
		}
		return bookSearchResponseDtos;
	}

}
