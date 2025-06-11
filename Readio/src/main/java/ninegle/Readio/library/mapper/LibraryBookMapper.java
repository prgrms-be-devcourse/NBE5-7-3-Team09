package ninegle.Readio.library.mapper;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.adapter.config.NCloudStorageConfig;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.library.domain.Library;
import ninegle.Readio.library.dto.book.AllLibraryBooksDto;
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto;
import ninegle.Readio.library.dto.book.LibraryDto;
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto;

@Component
@RequiredArgsConstructor
public class LibraryBookMapper {

	private final NCloudStorageConfig nCloudStorageConfig;

	//라이브러리에 책 추가
	public Long toNewLibraryBook(NewLibraryBookRequestDto libraryBookRequestDto) {
		return libraryBookRequestDto.bookId();
	}

	//라이브러리에 책 목록 가져오기
	public LibraryBookListResponseDto libraryBookListResponseDto(Library library, Page<Book> books) {
		List<AllLibraryBooksDto> allLibraryBooksDto = books.getContent().stream()
			.map(book -> AllLibraryBooksDto.builder()
				.bookId(book.getId())
				.bookName(book.getName())
				.bookImage(nCloudStorageConfig.toImageUrl(book.getImage()))
				.bookIsbn(book.getIsbn())
				.bookEcn(book.getEcn())
				.bookPubDate(book.getPubDate())
				.bookUpdateAt(book.getUpdatedAt())
				.rating(book.getRating())
				.build()
			).toList();

		LibraryDto libraryDto = LibraryDto.builder()
			.libraryId(library.getId())
			.libraryName(library.getLibraryName())
			.createdAt(library.getCreatedAt())
			.updatedAt(library.getUpdatedAt()).build();

		LibraryBookListResponseDto libraryBookResponseDto = LibraryBookListResponseDto.builder()
			.allLibraryBooks(allLibraryBooksDto)
			.libraryDto(libraryDto)
			.totalCount(books.getTotalElements())
			.size(books.getSize())
			.page(books.getNumber() + 1).build();
		return libraryBookResponseDto;
	}

}
