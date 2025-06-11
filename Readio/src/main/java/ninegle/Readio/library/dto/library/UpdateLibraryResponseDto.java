package ninegle.Readio.library.dto.library;

import lombok.Builder;

@Builder
public record UpdateLibraryResponseDto(long id, String libraryName) {
}
