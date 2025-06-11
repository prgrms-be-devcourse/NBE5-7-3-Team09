package ninegle.Readio.library.dto.library;

import lombok.Builder;

@Builder
public record NewLibraryResponseDto(long libraryId, String libraryName, long userId) {
}
