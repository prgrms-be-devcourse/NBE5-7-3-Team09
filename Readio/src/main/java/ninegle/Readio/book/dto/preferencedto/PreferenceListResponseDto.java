package ninegle.Readio.book.dto.preferencedto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.PaginationDto;

/**
 * Readio - BookPreferenceListDto
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@Builder
public record PreferenceListResponseDto(List<PreferenceResponseDto> preferences, PaginationDto pagination) {
}
