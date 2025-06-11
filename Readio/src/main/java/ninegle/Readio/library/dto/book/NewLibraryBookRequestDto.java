package ninegle.Readio.library.dto.book;

import jakarta.validation.constraints.NotNull;

public record NewLibraryBookRequestDto(
	@NotNull Long bookId) {
}
