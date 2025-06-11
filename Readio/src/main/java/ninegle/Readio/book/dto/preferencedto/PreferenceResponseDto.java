package ninegle.Readio.book.dto.preferencedto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Readio - BookPreferenceDto
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@Builder
public record PreferenceResponseDto(long id, String name, String image, BigDecimal rating) {
}
