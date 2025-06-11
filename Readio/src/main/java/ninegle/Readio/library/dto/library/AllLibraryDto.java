package ninegle.Readio.library.dto.library;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record AllLibraryDto(long id, String libraryName, LocalDateTime createAt, LocalDateTime updateAt) {
}
