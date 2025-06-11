package ninegle.Readio.library.dto.library;

import java.util.List;

import lombok.Builder;

@Builder
public record LibraryListResponseDto(List<AllLibraryDto> allLibraries, long totalCount, int page, int size) {
}
