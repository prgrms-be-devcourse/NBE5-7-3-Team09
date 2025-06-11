package ninegle.Readio.book.dto.reviewdto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Readio - ReviewSummaryDto
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Builder
public record ReviewSummaryDto(BigDecimal averageRating, int totalReviews) {
}
