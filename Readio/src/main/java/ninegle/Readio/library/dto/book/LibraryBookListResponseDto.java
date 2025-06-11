package ninegle.Readio.library.dto.book;

import java.util.List;

import lombok.Builder;

@Builder
public record LibraryBookListResponseDto(LibraryDto libraryDto, List<AllLibraryBooksDto> allLibraryBooks,
										 long totalCount, long page, long size) {

}
