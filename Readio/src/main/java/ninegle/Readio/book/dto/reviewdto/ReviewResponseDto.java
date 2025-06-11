package ninegle.Readio.book.dto.reviewdto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Readio - ReivewResponseDto
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Builder
public record ReviewResponseDto(long id, String email, BigDecimal rating, String text, LocalDateTime createdAt,
								LocalDateTime updatedAt) {
}
