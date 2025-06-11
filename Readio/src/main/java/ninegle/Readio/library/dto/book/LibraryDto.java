package ninegle.Readio.library.dto.book;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record LibraryDto(long libraryId, String libraryName, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
